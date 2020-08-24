package com.codingchili.bunneh.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.codingchili.bunneh.MainFragment
import com.codingchili.bunneh.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_login, container, false)

        // setup actions: authenticate
        // 1. auto select region based on GEO
                // change from login

        Glide.with(context)
            .load(R.drawable.bunny)
            .into(fragment.findViewById(R.id.app_logo))

        fragment.findViewById<Button>(R.id.button_login).setOnClickListener {
            done()
        }

        return fragment
    }

    fun done() {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.root, MainFragment())
            .commit()
    }
}