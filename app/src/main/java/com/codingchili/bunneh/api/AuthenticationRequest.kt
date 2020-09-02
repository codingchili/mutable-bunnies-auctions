package com.codingchili.bunneh.api

import com.codingchili.core.security.Account

data class AuthenticationRequest(
    val account: Account
) {
    val route = "authenticate"
    val target = "client.authentication.node"
}