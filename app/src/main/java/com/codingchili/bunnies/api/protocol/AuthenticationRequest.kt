package com.codingchili.bunnies.api.protocol

import com.codingchili.core.security.Account

/**
 * Authentication request for the authentication service.
 */
data class AuthenticationRequest(val account: Account) :
    ServerRequest(route = "authenticate", target = "client.authentication.node")