package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Inventory
import com.codingchili.bunneh.model.Notification
import com.codingchili.bunneh.model.Response
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

/**
 * Interface for the auction service.
 */
interface AuctionService {
    /**
     * Retrieves a list of auctions given a query string.
     */
    fun search(query: String): Single<List<Auction>>

    /**
     * Returns the current users bank inventory.
     */
    fun inventory(): Flowable<Inventory>

    /**
     * Puts an item up for auction with the given initial value.
     */
    fun auction(item: String, value: Int): Single<Response>

    /**
     * Places a bid on the given auction.
     */
    fun bid(value: Int, auctionId: String): Single<Response>

    /**
     * Subscribes to notifications from the server.
     */
    fun alerts(): Flowable<Notification>
}