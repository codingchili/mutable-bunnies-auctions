package com.codingchili.bunneh.api.protocol

import com.codingchili.core.protocol.ResponseStatus

open class ServerResponse(
    var status: ResponseStatus? = null,
    var message: String? = null
)