package com.codingchili.bunnies

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.codingchili.bunnies.api.Connector
import com.codingchili.bunnies.model.ContinentMapper
import com.codingchili.bunnies.ui.login.LoginFragment
import com.codingchili.bunnies.ui.search.SearchFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * The main activity is set up to handle activity based events such as the options menu
 * and back button navigation, logging out and clearing of the glide image cache.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var main: Fragment
    private var region: String? = null

    fun setRegion(region: String) {
        this.region = region
        updateTitle()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("foo", "onRestart")
        logout()
    }

    override fun onStop() {
        super.onStop()
        Log.e("foo", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("foo", "onDestroy")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ContinentMapper.init(resources)
        setContentView(R.layout.activity_main)

        //logout()

        if (Connector.token == null) {
            Log.e("foo", "created login fragment")
            main = LoginFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.root, main)
                .commit()
        } else {
            Log.e("foo", "created main fragment")
            MainFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> logout()
            R.id.menu_clear_cache -> clearCache()
        }
        return true
    }

    private fun clearCache() {
        Glide.get(applicationContext).clearMemory()
        GlobalScope.launch {
            Glide.get(applicationContext).clearDiskCache();
        }
    }

    private fun logout() {
        Log.e("foo", "logout called")
        // clear out any old data which may be associated with another user.
        //viewModelStore.clear()
        //Connector.token = null

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