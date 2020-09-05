package com.codingchili.bunnies.api

import com.codingchili.bunnies.api.protocol.AuthenticationRequest
import com.codingchili.bunnies.api.protocol.AuthenticationResponse
import com.codingchili.bunnies.model.single
import com.codingchili.core.security.Account
import com.codingchili.core.security.Token
import io.reactivex.rxjava3.core.Single

/**
 * Authentication service which connects to the authentication service through
 * the gateway router over REST using the Backend class.
 */
class BackendAuthenticationService : AuthenticationService {
    private var authentication: AuthenticationResponse? = null

    override fun authenticate(username: String, password: String): Single<AuthenticationResponse> {
        val account = Account(username, password)
        val single = single(
            Backend.request(
                AuthenticationRequest(account),
                AuthenticationResponse::class.java
            )
        )
        // save authentication information for later.
        single.subscribe { response, e -> authentication = response }
        return single
    }

    override fun token(): Token? {
        return authentication?.token
    }

    override fun logout() {
        authentication = null
    }

    override fun user(): String? {
        return authentication?.account?.username
    }

}