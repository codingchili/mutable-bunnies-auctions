package com.codingchili.bunneh

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingchili.bunneh.ui.details.DetailFragment
import com.codingchili.bunneh.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.root, DetailFragment())
            .commit()
    }

}