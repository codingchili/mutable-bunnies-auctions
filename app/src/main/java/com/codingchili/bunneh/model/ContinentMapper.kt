package com.codingchili.bunneh.model

import android.content.res.Resources
import com.codingchili.bunneh.R
import org.json.JSONObject

/**
 *
 */
class ContinentMapper {
    companion object {
        private lateinit var mapping: JSONObject

        fun init(resources: Resources) {
            mapping = JSONObject(resources.openRawResource(R.raw.continents)
                .bufferedReader().use { it.readText() })
        }

        fun fromCountryCode(code: String?): String? {
            return if (code != null && mapping.has(code)) {
                mapping.optString(code)
            } else {
                null
            }

        }
    }
}