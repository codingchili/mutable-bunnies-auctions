package com.codingchili.bunnies

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


/**
 * The main fragment hosts the bottom navigation bar and doesnt provide any other
 * useful layout on its own.
 */
class MainFragment : Fragment() {
    private lateinit var navController: NavController
    private var destination: Int? = null

    companion object {
        const val TAG = "main"
        private const val STATE_KEY = "nav_destination"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // called twice, one where savedInstance is null, idk wat.
        destination = savedInstanceState?.getInt(STATE_KEY) ?: destination
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_KEY, navController.currentDestination?.id ?: R.id.navigation_search)
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

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.mobile_navigation)

        if (destination == null) {
            graph.startDestination = R.id.navigation_search
        } else {
            graph.startDestination = destination!!
        }

        navController.graph = graph

        (activity as AppCompatActivity)
            .setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        return fragment
    }
}