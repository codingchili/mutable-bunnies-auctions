package com.codingchili.bunneh.ui

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.widget.TextView
import android.widget.Toast

/**
 * Used to show Toasts with styling improved for legibility.
 */
class AppToast {
    companion object {
        /**
         * Immediately shows a toast with the given text.
         *
         * @param context required to show toasts.
         * @param text the text to show in the toaster.
         * @param duration allows to set the duration of the toast, either
         * Toast.LENGTH_SHORT or Toast.LENGTH_LONG.
         */
        fun show(context: Context?, text: String?, duration: Int = Toast.LENGTH_SHORT) {
            if (text == null) {
                Log.i(
                    AppToast::class.java.name,
                    "no text provided for toast - suppressed. (likely an exception without message)"
                )
            } else {
                if (context != null) {
                    val toast = Toast.makeText(context, text, duration)
                    val view = toast.view

                    // set toast text color to white.
                    (toast.view.findViewById(android.R.id.message) as TextView)
                        .setTextColor(Color.WHITE)

                    // change toast background color to stand out more from the background.
                    view.background.colorFilter =
                        PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN)

                    toast.show()
                } else {
                    Log.i(
                        AppToast::class.java.name,
                        "context is no longer active - suppressed '${text}'."
                    )
                }
            }
        }
    }
}