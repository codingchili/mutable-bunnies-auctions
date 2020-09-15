package com.codingchili.bunnies.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codingchili.bunnies.MainActivity
import com.codingchili.bunnies.MainFragment
import com.codingchili.bunnies.R
import com.codingchili.bunnies.api.AuthenticationService
import com.codingchili.bunnies.api.Connector
import com.codingchili.bunnies.model.ContinentMapper
import com.codingchili.bunnies.ui.AppToast
import com.codingchili.bunnies.ui.dialog.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.*
import java.util.function.Consumer

/**
 * This fragment handles user login and server selection. Users may also
 * open the website to handle user registration. It is possible to sign up
 * using the application by using the 'register' route of the authentication service
 * but this doesn't make sense - as users should primarily create account on the game
 * website. Their inventory will be blank when signing up from the application otherwise.
 */
class LoginFragment : Fragment() {
    private val region: MutableLiveData<String> by lazy { MutableLiveData<String>(getString(R.string.unset)) }
    private val authentication = AuthenticationService.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Connector.protocol = getString(R.string.server_http)
        Connector.api_port = getString(R.string.api_port)
        Connector.web_port = getString(R.string.web_port)
    }

    private fun assertServerRegionSet(block: () -> Unit) {
        if (Connector.server == null) {
            AppToast.show(context, getString(R.string.no_region_selected))
        } else {
            block.invoke()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_login, container, false)
        val regionSelector = fragment.findViewById<MaterialButton>(R.id.server_region)

        fragment.findViewById<View>(R.id.button_register).setOnClickListener {
            assertServerRegionSet {
                val server = "${Connector.protocol}${Connector.server}:${Connector.web_port}"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(server)))
            }
        }

        fragment.findViewById<TextInputEditText>(R.id.edit_password)
            .setOnEditorActionListener { _, _, _ ->
                authenticate(fragment)
                true
            }

        regionSelector.setOnClickListener {
            NavigableTreeDialog(
                R.string.dialog_server_region,
                serverRegionTree,
                listener = Consumer<NavigableTree> {
                    Connector.server = getString(it.resource!!)
                    region.value = getString(it.name)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        Glide.with(requireContext())
            .load(R.drawable.bunny)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(fragment.findViewById(R.id.app_logo))

        fragment.findViewById<Button>(R.id.button_login)
            .setOnClickListener {
                assertServerRegionSet {
                    authenticate(fragment)
                }
            }

        region.observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).setRegion(it)
            // format readable name to abbreviation to make the layout lighter.
            if (it.length > 1) {
                val region = it.split(" ")
                regionSelector.text = when (region.size) {
                    1 -> region[0].substring(0, 2).toUpperCase(Locale.ROOT)
                    2 -> region.map { r -> r[0] }.joinToString("")
                    else -> "…"
                }
            }
        })
        attemptLocationAccess()

        return fragment
    }

    private fun attemptLocationAccess() {
        val permission = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val request =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    retrieveServerRegionFromLocation()
                } else {
                    // no default region, creates massive confusion for the rest of the world.
                    //region.value = getString(R.string.server_region_default)
                }
            }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) ==
                    PackageManager.PERMISSION_GRANTED -> {
                retrieveServerRegionFromLocation()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                InformationDialog(
                    R.string.location_title,
                    R.string.location_text,
                    listener = Runnable {
                        request.launch(permission)
                    }).show(requireActivity().supportFragmentManager, Dialogs.TAG)
            }
            else -> {
                request.launch(permission)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun retrieveServerRegionFromLocation() {
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Thread {
            try {
                val location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val coder = Geocoder(context, Locale.getDefault())

                if (location != null) {
                    val address =
                        coder.getFromLocation(location.latitude, location.longitude, 1);
                    val code = address.firstOrNull()?.countryCode
                    val continent = ContinentMapper.fromCountryCode(code)

                    if (continent != null) {
                        Log.i(javaClass.name, "Resolved continent $continent from country $code")
                        Connector.server = getString(
                            resources.getIdentifier(
                                "server_${continent.toLowerCase(Locale.ROOT)}",
                                "string",
                                requireActivity().packageName
                            )
                        )
                        region.postValue(continent)
                    } else {
                        Log.e(javaClass.name, "No country code mapping from $code to continent.")
                    }
                }
            } catch (e: Throwable) {
                AppToast.show(context, e.message)
            }
        }.run()
    }


    private fun authenticate(fragment: View) {
        val username = fragment.findViewById<TextInputLayout>(R.id.edit_username)
            .editText!!.text.toString()
        val password = fragment.findViewById<TextInputLayout>(R.id.edit_password_container)
            .editText!!.text.toString()

        val overlay = fragment.findViewById<View>(R.id.overlay)
        overlay.visibility = View.VISIBLE

        authentication.authenticate(username, password).bindToLifecycle(fragment)
            .subscribe { authentication, e ->
                if (e == null) {
                    Connector.token = authentication.token
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.root, MainFragment())
                        .addToBackStack(MainFragment.TAG)
                        .commit()
                } else {
                    AppToast.show(context, e.message)
                }
                overlay.visibility = View.GONE
            }
    }
}