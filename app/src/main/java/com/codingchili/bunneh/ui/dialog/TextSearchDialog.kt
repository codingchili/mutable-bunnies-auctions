package com.codingchili.bunneh.ui.dialog

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunneh.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class TextSearchDialog : DialogFragment() {

    private fun searchHandler(): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                dismiss()
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
        val view = inflater.inflate(R.layout.dialog_text_search, container, false)
        val search = view.findViewById<TextInputEditText>(R.id.search_text)

        view.findViewById<MaterialButton>(R.id.close_button).setOnClickListener { dismiss() }

        search.setOnEditorActionListener(searchHandler())
        search.requestFocus()

        Handler().postDelayed({
            (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }, 125)

        return view
    }
}