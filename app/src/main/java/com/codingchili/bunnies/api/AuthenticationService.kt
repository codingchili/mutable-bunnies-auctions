package com.codingchili.bunnies.api

import com.codingchili.bunnies.api.protocol.AuthenticationResponse
import com.codingchili.core.security.Token
import io.reactivex.rxjava3.core.Single

/**
 * Interface for the authentication service which handles user authentication.
 * Users must be authenticated and receive a token in order to interact with
 * other services which require authentication. The user may still only interact
 * with services that are externally available, marked as external in the router
 * service configuration.
 *
 * The token is stateless but has an expiry date that should be checked. Note:
 * This is currently not the case.
 */
interface AuthenticationService {
    companion object {
        // change this to use the remote server backend, not included in assignment.
        val instance = BackendAuthenticationService()
    }

    /**
     * Attempts to authenticate the given user, returns an error message should the
     * authentication attempt fail.
     *
     * @param username used for authentication.
     * @param password used for authentication.
     * @return a single emitted when the request completes.
     */
    fun authenticate(username: String, password: String): Single<AuthenticationResponse>

    /**
     * Clears any information associated with the user such as username, password and token.
     * This does not clear any viewmodels this is to be handled by the activity.
     */
    fun logout()

    /**
     * Returns the username of the currently logged in user if any.
     */
    fun user(): String?

    /**
     * Returns the token of the currently logged in user if any.
     */
    fun token(): Token?
}