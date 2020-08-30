package com.codingchili.bunneh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {
    private lateinit var navController: NavController

    companion object {
        const val TAG = "main"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_main, container, false)
        val navView: BottomNavigationView = fragment.findViewById(R.id.nav_view)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_search,
                R.id.navigation_inventory,
                R.id.navigation_auctions,
                R.id.navigation_notifications
            )
        )

        (activity as AppCompatActivity)
            .setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        return fragment
    }

    public fun getCurrent(): String {
        return resources.getResourceName(navController.currentDestination!!.id)
    }
}