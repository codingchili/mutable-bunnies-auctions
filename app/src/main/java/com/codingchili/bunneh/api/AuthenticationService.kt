package com.codingchili.bunneh.api

import com.codingchili.core.security.Account
import com.codingchili.core.security.Token
import io.reactivex.rxjava3.core.Single

/**
 * Performs authentication of users.
 */
interface AuthenticationService {
    companion object {
        val instance = BackendAuthenticationService()
    }

    /**
     * Attempts to authenticate the given user, returns an error message should the
     * authentication attempt fail.
     *
     * @param username used for authentication.
     * @param password used for authentication.
     */
    fun authenticate(username: String, password: String): Single<AuthenticationResponse>

    /**
     * Clear login state.
     */
    fun logout()

    /**
     *
     */
    fun user(): String?

    /**
     *
     */
    fun token(): Token?
}