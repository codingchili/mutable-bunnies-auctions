package com.codingchili.bunneh.api

import android.util.Log

class Connector {
    companion object {
        var server: String? = null
            set(value) {
                Log.i("connector", "server set to $value")
                field = value
            }
    }
}