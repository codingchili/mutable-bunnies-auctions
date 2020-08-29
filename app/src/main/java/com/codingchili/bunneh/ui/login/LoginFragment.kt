package com.codingchili.bunneh.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.codingchili.bunneh.MainFragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.LocalAuthenticationService
import com.codingchili.bunneh.ui.dialog.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import java.util.function.Consumer


class LoginFragment : Fragment() {
    val region: MutableLiveData<String> by lazy { MutableLiveData<String>(getString(R.string.server_region)) }
    val authentication = LocalAuthenticationService.instance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_login, container, false)
        val regionSelector = fragment.findViewById<MaterialButton>(R.id.server_region)

        regionSelector.text = region.value
        regionSelector.setOnClickListener {
            NavigableTreeDialog(
                "Server region",
                serverRegionTree,
                listener = Consumer<NavigableTree> {
                    region.value = it.name
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        Glide.with(requireContext())
            .load(R.drawable.bunny)
            .into(fragment.findViewById(R.id.app_logo))

        fragment.findViewById<Button>(R.id.button_login)
            .setOnClickListener { authenticate(fragment) }

        region.observe(viewLifecycleOwner, Observer { regionSelector.text = it })
        region.value = "onCreateView"
        attemptLocationAccess()

        return fragment
    }

    private fun attemptLocationAccess() {
        region.value = "update"
        val permission = android.Manifest.permission.ACCESS_COARSE_LOCATION

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) ==
                    PackageManager.PERMISSION_GRANTED -> retrieveServerRegionFromLocation()

            shouldShowRequestPermissionRationale(permission) -> {
                // triggered when not granted
            }
            else -> {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted) {
                        retrieveServerRegionFromLocation()
                    } else {
                        InformationDialog(R.string.location_title, R.string.location_text)
                            .show(requireActivity().supportFragmentManager, Dialogs.TAG)
                    }
                }.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
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
                    region.postValue(address.first().countryName)
                }
            } catch (e: Throwable) {
                Log.e("foo", e.message!!)
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

        authentication.authenticate(username, password).subscribe { authentication, error ->
            if (error == null) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.root, MainFragment())
                    .addToBackStack("main")
                    .commit()
                overlay.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}