package com.codingchili.bunnies.api.protocol

/**
 * Base class for server requests, all requests requires
 * a target, which is used to indicate to the gateway service
 * where it should be forwarded over the cluster. The route parameter
 * indicates which method on the target service to invoke.
 */
open class ServerRequest(val route: String, val target: String)