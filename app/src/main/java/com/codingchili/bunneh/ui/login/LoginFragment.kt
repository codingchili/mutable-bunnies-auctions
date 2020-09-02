package com.codingchili.bunneh.ui.login

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
import com.codingchili.bunneh.MainActivity
import com.codingchili.bunneh.MainFragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuthenticationService
import com.codingchili.bunneh.api.Connector
import com.codingchili.bunneh.model.ContinentMapper
import com.codingchili.bunneh.ui.AppToast
import com.codingchili.bunneh.ui.dialog.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.*
import java.util.function.Consumer


class LoginFragment : Fragment() {
    private val region: MutableLiveData<String> by lazy { MutableLiveData<String>(getString(R.string.unset)) }
    private val authentication = AuthenticationService.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Connector.protocol = getString(R.string.server_http)
        Connector.api_port = getString(R.string.api_port)
        Connector.web_port = getString(R.string.web_port)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_login, container, false)
        val regionSelector = fragment.findViewById<MaterialButton>(R.id.server_region)

        fragment.findViewById<View>(R.id.button_register).setOnClickListener {
            val server = "${Connector.protocol}${Connector.server}:${Connector.web_port}"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(server)))
        }

        regionSelector.setOnClickListener {
            NavigableTreeDialog(
                "Server region",
                serverRegionTree,
                listener = Consumer<NavigableTree> {
                    Connector.server = getString(it.resource!!)
                    region.value = it.name
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        Glide.with(requireContext())
            .load(R.drawable.bunny)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(fragment.findViewById(R.id.app_logo))

        fragment.findViewById<Button>(R.id.button_login)
            .setOnClickListener {
                if (Connector.server != null) {
                    authenticate(fragment)
                } else {
                    AppToast.show(context, getString(R.string.no_region_selected))
                }
            }

        region.observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).setRegion(it)
            regionSelector.text = it
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
        val password = fragment.findViewById<TextInputLayout>(R.id.edit_password)
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