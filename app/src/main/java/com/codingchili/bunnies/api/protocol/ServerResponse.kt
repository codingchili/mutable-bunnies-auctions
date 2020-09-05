package com.codingchili.bunnies.api.protocol

import com.codingchili.core.protocol.ResponseStatus

/**
 * A response from the server.
 *
 * Responses always contain at least a response status code and optionally
 * a message.
 */
open class ServerResponse(
    var status: ResponseStatus? = null,
    var message: String? = null
)