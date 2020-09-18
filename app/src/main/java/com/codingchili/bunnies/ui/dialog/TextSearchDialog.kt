package com.codingchili.bunnies.ui.dialog

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
import com.codingchili.bunnies.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.function.Consumer

/**
 * Dialog used to retrieve text from the user, when the dialog completes either
 * through ime action then the listener is notified with the input value.
 *
 * This is primarily used to request a free-text search query.
 */
class TextSearchDialog(private val listener: Consumer<String>) : DialogFragment() {

    override fun onStop() {
        dismissAllowingStateLoss()
        super.onStop()
    }

    private fun searchHandler(): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                listener.accept(v.text.toString())
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

        // requires a delay otherwise the keyboard will appear then disappear immediately.
        Handler().postDelayed({
            (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }, 125)

        return view
    }
}