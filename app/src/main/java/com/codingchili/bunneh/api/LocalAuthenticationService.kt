package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Authentication
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Mock implementation of the authentication service.
 */
class LocalAuthenticationService: AuthenticationService {
    override fun authenticate(username: String, password: String): Future<Authentication> {
        return CompletableFuture.completedFuture(Authentication(token = "foo"))
    }
}