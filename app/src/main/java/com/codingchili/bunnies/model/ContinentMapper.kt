package com.codingchili.bunnies.model

import android.content.res.Resources
import com.codingchili.bunnies.R
import org.json.JSONObject

/**
 * Maps country codes to continents (server regions) used for
 * detecting which server region to choose from a location.
 */
class ContinentMapper {
    companion object {
        private lateinit var mapping: JSONObject

        /**
         * Init needs to be called to grant access to application resources where
         * mappings are stored in a json format.
         *
         * @param application resources for raw access.
         */
        fun init(resources: Resources) {
            mapping = JSONObject(resources.openRawResource(R.raw.continents)
                .bufferedReader().use { it.readText() })
        }

        /**
         * Determines which server region the country code is placed in.
         * @param code a two letter country code.
         * @return a two letter region code or null as this method cannot
         * provide a reasonable default server region across the globe.
         */
        fun fromCountryCode(code: String?): String? {
            return if (code != null && mapping.has(code)) {
                mapping.optString(code)
            } else {
                null
            }

        }
    }
}