package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Inventory
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.Notification
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

/**
 * Interface for the auction service.
 */
interface AuctionService {

    companion object {
        var instance = LocalAuctionService()
    }

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
    fun auction(item: Item, value: Int): Single<Auction>

    /**
     * Places a bid on the given auction.
     */
    fun bid(value: Int, auction: Auction): Single<Auction>

    /**
     * Subscribes to notifications from the server.
     */
    fun notifications(): Single<List<Notification>>

    /**
     * Retrieve an auction by its id.
     */
    fun findById(auctionId: String): Single<Auction>
}