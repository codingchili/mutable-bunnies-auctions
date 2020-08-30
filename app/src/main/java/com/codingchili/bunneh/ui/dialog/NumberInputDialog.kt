package com.codingchili.bunneh.ui.dialog

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunneh.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.function.Consumer

class NumberInputDialog(var listener: Consumer<Int>? = null) : DialogFragment() {

    private fun searchHandler(): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                dismiss()
                listener?.accept(Integer.parseInt(v.text.replace(Regex(","), "")))
                return@OnEditorActionListener true
            }
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_number_input, container, false)
        val search = view.findViewById<TextInputEditText>(R.id.input_text)

        search.setOnEditorActionListener(searchHandler())
        search.requestFocus()

        view.findViewById<MaterialButton>(R.id.close_button).setOnClickListener { dismiss() }

        Handler().postDelayed({
            (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }, 125)

        return view
    }

}