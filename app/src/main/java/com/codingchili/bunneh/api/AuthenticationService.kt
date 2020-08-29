package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Authentication
import io.reactivex.rxjava3.core.Single

/**
 * Performs authentication of users.
 */
interface AuthenticationService {
    /**
     * Attempts to authenticate the given user, returns an error message should the
     * authentication attempt fail.
     *
     * @param username used for authentication.
     * @param password used for authentication.
     */
    fun authenticate(username: String, password: String): Single<Authentication>

    /**
     * Return current login state.
     */
    fun current(): Authentication?

    /**
     * Clear login state.
     */
    fun logout()
}