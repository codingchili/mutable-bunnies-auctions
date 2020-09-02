package com.codingchili.bunneh.api.protocol

import com.codingchili.bunneh.api.protocol.ServerResponse
import com.codingchili.core.security.Account
import com.codingchili.core.security.Token

class AuthenticationResponse: ServerResponse() {
    var token: Token? = null
    var account: Account? = null
}