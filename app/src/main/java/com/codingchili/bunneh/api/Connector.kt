package com.codingchili.bunneh.api

import android.util.Log

import com.codingchili.core.security.Token

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