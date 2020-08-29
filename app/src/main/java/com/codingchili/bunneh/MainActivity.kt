package com.codingchili.bunneh

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codingchili.bunneh.ui.login.LoginFragment


class MainActivity : AppCompatActivity() {
    private lateinit var main: Fragment
    private var region : String? = null

    fun setRegion(region: String) {
        this.region = region
        updateTitle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.root, main)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            logout()
        }
        return true
    }

    private fun logout() {
        updateTitle()
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.root, LoginFragment())
            .commit()
    }

    private fun updateTitle() {
        title = if (region == null) {
            resources.getString(R.string.app_name)
        } else {
            "${resources.getString(R.string.app_name)} - $region"
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            updateTitle()
        } else {
            finish()
        }
    }
}