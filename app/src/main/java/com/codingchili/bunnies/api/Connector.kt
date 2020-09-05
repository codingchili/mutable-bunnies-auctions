package com.codingchili.bunnies.api

import android.util.Log

import com.codingchili.core.security.Token

/**
 * Global state to keep track of servers, avoids shuffling contexts into
 * pure networking classes.
 */
class Connector {
    companion object {
        var token: Token? = null
        var protocol: String? = null
        var api_port: String? = null
        var web_port: String? = null
        var server: String? = null
            set(value) {
                Log.i("connector", "server set to $value")
                field = value
            }
    }
}