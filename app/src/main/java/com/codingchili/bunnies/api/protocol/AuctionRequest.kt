package com.codingchili.bunnies.api.protocol

import com.codingchili.banking.model.Item
import com.codingchili.banking.model.QueryType
import com.codingchili.core.security.Token

/**
 * A request for the auction.node microservice which handles auctions.
 */
class AuctionRequest(
    route: String,
    val params: String? = null,
    val auctionId: String? = null,
    val item: Item? = null,
    val value: Int = 0,
    val add: Boolean = false,
    val query: QueryType? = null
) : ServerRequest(route = route, target = "auction.node") {
    var token: Token? = null
}