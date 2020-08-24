package com.codingchili.bunneh

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var main: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main = MainFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.root, main)
            .addToBackStack("main")
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            title = main.getCurrent()
        } else {
            finish()
        }
    }
}