package com.codingchili.bunneh.api

import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.Inventory
import com.codingchili.banking.model.Item
import com.codingchili.bunneh.model.Notification
import com.codingchili.bunneh.model.Response
import io.reactivex.rxjava3.core.Single

class BackendAuctionService: AuctionService {
    override fun search(query: String): Single<List<Auction>> {
        TODO("Not yet implemented")
    }

    override fun inventory(): Single<Inventory> {
        TODO("Not yet implemented")
    }

    override fun auction(item: Item, value: Int): Single<Auction> {
        TODO("Not yet implemented")
    }

    override fun bid(value: Int, auction: Auction): Single<Auction> {
        TODO("Not yet implemented")
    }

    override fun notifications(): Single<List<Notification>> {
        TODO("Not yet implemented")
    }

    override fun findById(auctionId: String): Single<Auction> {
        TODO("Not yet implemented")
    }

    override fun favorite(auction: Auction, add: Boolean): Single<Response> {
        TODO("Not yet implemented")
    }

    override fun favorites(): Single<Set<Auction>> {
        TODO("Not yet implemented")
    }

}