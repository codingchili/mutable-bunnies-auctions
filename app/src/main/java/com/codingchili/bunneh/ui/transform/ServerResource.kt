package com.codingchili.bunneh.ui.transform

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codingchili.bunneh.R

/**
 * Loads resources from the server.
 */
class ServerResource {
    companion object {
        /**
         * Loads an icon from the resource server asynchronously.
         *
         */
        fun icon(view: ImageView, resource: String) {
            val host = view.context.getString(R.string.resources_cdn)
            Glide.with(view.context)
                .load("$host/resources/gui/item/icon/${resource}")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        }

    }
}