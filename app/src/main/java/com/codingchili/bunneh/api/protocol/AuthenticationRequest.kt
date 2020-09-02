package com.codingchili.bunneh.api.protocol

import com.codingchili.bunneh.api.protocol.ServerRequest
import com.codingchili.core.security.Account

data class AuthenticationRequest(val account: Account) :
    ServerRequest(route = "authenticate", target = "client.authentication.node")