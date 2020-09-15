package com.codingchili.bunnies.api

import com.codingchili.banking.model.*
import com.codingchili.bunnies.api.protocol.AuctionRequest
import com.codingchili.bunnies.api.protocol.AuctionResponse
import com.codingchili.bunnies.api.protocol.ServerResponse
import com.codingchili.bunnies.model.single
import io.reactivex.rxjava3.core.Single

/**
 * Implementation of the auction service that connects to a microservice over
 * REST through the gateway router using the Backend class.
 */
class BackendAuctionService : AuctionService {

    private fun send(request: AuctionRequest): Single<AuctionResponse> {
        request.token = AuthenticationService.instance.token()
        return single(Backend.request(request, AuctionResponse::class.java))
    }

    override fun search(query: QueryType, param: String?): Single<List<Auction>> {
        return send(
            AuctionRequest(
                route = "search",
                query = query,
                params = param
            )
        ).map { it.auctions }
    }

    override fun inventory(): Single<Inventory> {
        return send(AuctionRequest(route = "inventory"))
            .map { it.inventory }
    }

    override fun auction(item: Item, value: Int): Single<Auction> {
        return send(
            AuctionRequest(
                route = "auction",
                itemId = item.id,
                value = value
            )
        ).map { it.auctions!!.first() }
    }

    override fun bid(value: Int, auction: Auction): Single<Auction> {
        return send(
            AuctionRequest(
                route = "bid",
                auctionId = auction.id,
                value = value
            )
        ).map { it.auctions!!.first() }
    }

    override fun notifications(): Single<List<Notification>> {
        return send(AuctionRequest(route = "notifications")).map { it.notifications }
    }

    override fun findById(auctionId: String): Single<Auction> {
        return send(
            AuctionRequest(
                route = "findById",
                auctionId = auctionId
            )
        ).map { it.auctions!!.first() }
    }

    override fun favorite(auction: Auction, add: Boolean): Single<ServerResponse> {
        return send(
            AuctionRequest(
                route = "favorite",
                auctionId = auction.id,
                add = add
            )
        ).map { it as ServerResponse }
    }

    override fun favorites(): Single<Set<Auction>> {
        return send(AuctionRequest(route = "favorites")).map { it.auctions!!.toSet() }
    }
}