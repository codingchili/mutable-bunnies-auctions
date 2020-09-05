package com.codingchili.bunnies.ui.transform

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codingchili.bunnies.R

/**
 * Loads resources from the server.
 */
class ServerResource {
    companion object {
        /**
         * Loads an icon from the resource server asynchronously.
         * @param view the view to load the image into.
         * @param resource the filename of the icon resource to load including extension.
         */
        fun icon(view: ImageView, resource: String) {
            val host = view.context.getString(R.string.resources_cdn)
            Glide.with(view.context)
                .load("$host/icon/${resource}")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        }

    }
}