package com.codingchili.bunnies.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunnies.R
import com.google.android.material.button.MaterialButton

/**
 * The information dialog shows a simple dialog with a header, some text and a close button.
 */
class InformationDialog(var title: Int, var text: Int, var listener: Runnable? = null) :
    DialogFragment() {

    override fun onStop() {
        dismissAllowingStateLoss()
        super.onStop()
    }

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