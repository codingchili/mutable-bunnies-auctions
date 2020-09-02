package com.codingchili.bunneh.api.protocol

import com.codingchili.banking.model.Item
import com.codingchili.core.security.Token

class AuctionRequest(
    route: String,
    val params: String? = null,
    val auctionId: String? = null,
    val item: Item? = null,
    val value: Int = 0,
    val add: Boolean = false
) : ServerRequest(route = route, target = "auction.node") {
    var token: Token? = null
}