package com.codingchili.bunnies.api

import com.codingchili.banking.model.*
import com.codingchili.bunnies.api.mock.LocalAuctionService
import com.codingchili.bunnies.api.protocol.ServerResponse
import io.reactivex.rxjava3.core.Single

/**
 * Interface for the auction service, used to query and create auctions.
 * The auction service is available through the gateway router to clients
 * after authenticating with the authentication service.
 */
interface AuctionService {

    companion object {
        // change this to use the remote server instead - not included in assignment.
        var instance = LocalAuctionService()
    }

    /**
     * Performs a search for auctions by specifying the query type and an optional
     * parameter for the search type. QueryType is used to reference a query
     * on the server. While it is possible to use Query<>()..toString()
     * to send queries to the server, this isn't reusable for other clients.
     *
     * @param query the type of query to perform on the server.
     * @param param optional paramter for the search type.
     * @return a single to be emitted when the request completes.
     */
    fun search(query: QueryType, param: String? = null): Single<List<Auction>>

    /**
     * Returns the current users bank inventory.
     *
     * @return a single to be emitted when the request completes.
     */
    fun inventory(): Single<Inventory>

    /**
     * Puts an item up for auction with the given initial value.
     * @param item a reference to the item to be sold, only the ID should be sent to the server.
     * @param value the initial value of the auction, bids under this value are not accepted.
     * @return a single to be emitted when the request completes.
     */
    fun auction(item: Item, value: Int): Single<Auction>

    /**
     * Places a bid on the given auction.
     *
     * @param value the value of the bid.
     * @param auction the auction the bid is to be placed on.
     * @return a single to be emitted when the request completes.
     */
    fun bid(value: Int, auction: Auction): Single<Auction>

    /**
     * Retrieves the current users last notifications from the server.
     * @return a single to be emitted when the request completes.
     */
    fun notifications(): Single<List<Notification>>

    /**
     * Retrieve an auction by its id.
     *
     * @param auctionId the id of the auction to retrieve from the server.
     * @return a single to be emitted when the request completes.
     */
    fun findById(auctionId: String): Single<Auction>

    /**
     * Marks or unmarks the given auction as a favorite.
     * @param auction the auction to change favorite state of.
     * @param add true if the auction should be added to favorites, otherwise it is removed.
     */
    fun favorite(auction: Auction, add: Boolean): Single<ServerResponse>

    /**
     * @return lists the users favorited auctions, the server may limit the amount of
     * returned items by end date.
     */
    fun favorites(): Single<Set<Auction>>
}