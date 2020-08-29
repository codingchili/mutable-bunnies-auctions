package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Authentication
import com.codingchili.bunneh.model.User
import com.codingchili.bunneh.model.single
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Mock implementation of the authentication service.
 */
class LocalAuthenticationService : AuthenticationService {
    private var authenticated: Authentication? = null

    override fun authenticate(username: String, password: String): Single<Authentication> {
        authenticated = Authentication(
            token = "foo",
            user = User(username.toLowerCase(), username)
        )
        return single(CompletableFuture.supplyAsync {
            Thread.sleep(1500)
            authenticated!!
        })
    }

    override fun current(): Authentication? {
        // if token expired return null.
        return authenticated
    }

    override fun logout() {
        authenticated = null
    }
}