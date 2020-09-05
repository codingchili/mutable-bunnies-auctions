package com.codingchili.bunnies.api.protocol

import com.codingchili.core.security.Account
import com.codingchili.core.security.Token

/**
 * Authentication response from the authentication service.
 * Token contains the appwide token which has permissions to
 * access the auction service.
 */
class AuthenticationResponse : ServerResponse() {
    var token: Token? = null
    var account: Account? = null
}