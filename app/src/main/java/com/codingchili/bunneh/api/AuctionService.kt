package com.codingchili.bunneh.api

import io.reactivex.rxjava3.core.Single
import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.Item
import com.codingchili.banking.model.Inventory
import com.codingchili.bunneh.model.Notification
import com.codingchili.bunneh.model.Response

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
    fun inventory(): Single<Inventory>

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

    /**
     *
     */
    fun favorite(auction: Auction, add: Boolean): Single<Response>

    /**
     *
     */
    fun favorites(): Single<Set<Auction>>
}