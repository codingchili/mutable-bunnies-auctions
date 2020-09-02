package com.codingchili.bunneh.api

import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.Inventory
import com.codingchili.banking.model.Item
import com.codingchili.banking.model.Notification
import com.codingchili.bunneh.api.protocol.AuctionRequest
import com.codingchili.bunneh.api.protocol.AuctionResponse
import com.codingchili.bunneh.api.protocol.ServerResponse
import com.codingchili.bunneh.model.single
import io.reactivex.rxjava3.core.Single

class BackendAuctionService : AuctionService {

    private fun send(request: AuctionRequest): Single<AuctionResponse> {
        request.token = AuthenticationService.instance.token()
        return single(Backend.request(request, AuctionResponse::class.java))
    }

    override fun search(query: String): Single<List<Auction>> {
        return send(
            AuctionRequest(
                route = "search",
                params = query
            )
        )
            .map { it.auctions }
    }

    override fun inventory(): Single<Inventory> {
        return send(AuctionRequest(route = "inventory"))
            .map { it.inventory }
    }

    override fun auction(item: Item, value: Int): Single<Auction> {
        return send(
            AuctionRequest(
                route = "auction",
                item = item,
                value = value
            )
        ).map { it.auctions.first() }
    }

    override fun bid(value: Int, auction: Auction): Single<Auction> {
        return send(
            AuctionRequest(
                route = "bid",
                auctionId = auction.id,
                value = value
            )
        ).map { it.auctions.first() }
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
        ).map { it.auctions.first() }
    }

    override fun favorite(auction: Auction, add: Boolean): Single<ServerResponse> {
        return send(
            AuctionRequest(
                route = "favorite",
                auctionId = auction.id,
                add = add
            )
        )
            .map { it as ServerResponse }
    }

    override fun favorites(): Single<Set<Auction>> {
        return send(AuctionRequest(route = "favorites")).map { it.auctions.toSet() }
    }
}