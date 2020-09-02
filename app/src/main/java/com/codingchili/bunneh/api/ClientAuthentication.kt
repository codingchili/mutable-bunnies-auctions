package com.codingchili.bunneh.api

import com.codingchili.core.security.Account
import com.codingchili.core.security.Token

open class ClientAuthentication {
    var token: Token? = null
    var account: Account? = null
}