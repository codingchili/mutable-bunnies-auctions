package com.codingchili.bunnies.ui.dialog

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunnies.R
import com.codingchili.bunnies.ui.transform.formatValue
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.function.Consumer

/**
 * This dialog accepts a number input and formats it using the thousandth separator.
 * When the dialog completes successfully, either by ime action or accept button
 * a listener will be notified with the input value.
 */
class NumberInputDialog(var listener: Consumer<Int>? = null) : DialogFragment() {

    override fun onStop() {
        dismissAllowingStateLoss()
        super.onStop()
    }

    private fun searchHandler(): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { input, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                dismiss()
                if (input.text.isNotEmpty()) {
                    listener?.accept(Integer.parseInt(input.text.replace(Regex(","), "")))
                }
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

        search.addTextChangedListener(object : TextWatcher {
            // inline doesn't work as we need a reference to 'this'.
            override fun afterTextChanged(s: Editable?) {
                search.removeTextChangedListener(this)

                search.setText(formatValue(s.toString()))
                search.setSelection(search.text?.length ?: 0);

                search.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        search.setOnEditorActionListener(searchHandler())
        search.requestFocus()

        view.findViewById<MaterialButton>(R.id.close_button).setOnClickListener { dismiss() }

        // this is required in order to show the keyboard when the dialog appears, autofocus doesn't work.
        // if this isn't delayed, then the keyboard will appear and then disappear automatically.
        Handler().postDelayed({
            (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }, 125)

        return view
    }

}