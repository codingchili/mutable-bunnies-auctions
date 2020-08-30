package com.codingchili.bunneh.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunneh.R
import com.google.android.material.button.MaterialButton

class InformationDialog(var title: Int, var text: Int, var listener : Runnable? = null): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_information, container, false)

        view.findViewById<TextView>(R.id.dialog_title).text = getString(title)
        view.findViewById<TextView>(R.id.text).text = getString(text)

        view.findViewById<MaterialButton>(R.id.close_button).setOnClickListener {
            dismiss()
            listener?.run()
        }

        return view
    }

}