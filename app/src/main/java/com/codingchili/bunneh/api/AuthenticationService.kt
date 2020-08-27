package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Authentication
import java.util.concurrent.Future

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
    fun authenticate(username: String, password: String): Future<Authentication>
}