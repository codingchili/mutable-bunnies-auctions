package com.codingchili.bunnies.api.protocol

import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.Inventory
import com.codingchili.banking.model.Notification

/**
 * A response object for the auction microservice.
 */
class AuctionResponse(
    val inventory: Inventory?,
    val auctions: List<Auction>?,
    val notifications: List<Notification>?
) : ServerResponse()