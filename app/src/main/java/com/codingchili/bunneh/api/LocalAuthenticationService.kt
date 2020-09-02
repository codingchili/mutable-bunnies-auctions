package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.single
import com.codingchili.core.security.Account
import com.codingchili.core.security.Token
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.CompletableFuture

/**
 * Mock implementation of the authentication service.
 */
class LocalAuthenticationService : AuthenticationService {
    private var authenticated: AuthenticationResponse? = null

    override fun authenticate(username: String, password: String): Single<AuthenticationResponse> {
        authenticated = AuthenticationResponse()
        authenticated!!.account = Account(username, password)

        return single(CompletableFuture.supplyAsync {
            Thread.sleep(MockData.delay)
            if (password == username) {
                authenticated!!
            } else {
                throw Exception("Wrong password.")
            }
        })
    }

    override fun user(): String? {
        return authenticated?.account?.username
    }

    override fun token(): Token? {
        return authenticated?.token
    }

    override fun logout() {
        authenticated = null
    }
}