package com.codingchili.bunneh.api

import android.util.Log
import com.codingchili.bunneh.model.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.CompletableFuture

/**
 * Mock implementation of the auction service.
 */
class LocalAuctionService : AuctionService {
    private var auctions = AuctionMock.auctions
    private var inventory: Inventory = Inventory(
        items = listOf(AuctionMock.branch, AuctionMock.flamingStick, AuctionMock.spacewand),
        liquidity = 7600000,
        funds = 17600000
    )

    override fun search(query: String): Single<List<Auction>> {
        return when (query) {
            "null" -> {
                single(CompletableFuture.supplyAsync { listOf<Auction>() })
            }
            "error" -> {
                val future = CompletableFuture<List<Auction>>()
                future.completeExceptionally(Exception("Foo"))
                single<List<Auction>>(future)
            }
            else -> {
                single<List<Auction>>(CompletableFuture.supplyAsync {
                    Thread.sleep(1500)
                    auctions
                })
            }
        }
    }

    override fun inventory(): Flowable<Inventory> {
        return flow(CompletableFuture.supplyAsync { inventory })
    }

    override fun auction(item: Item, value: Int): Single<Auction> {
        return single(CompletableFuture.supplyAsync {
            val seller = AuthenticationService.instance.current()!!
            val auction = Auction(item = item, initial = value, seller = seller.user)

            inventory.items = inventory.items.filterNot { it == item }
            auctions.add(auction)

            auction
        })
    }

    override fun bid(value: Int, auction: Auction): Single<Auction> {
        return single(CompletableFuture.supplyAsync {
            val owner = AuthenticationService.instance.current()!!.user

            Log.e("foo", "high bid = ${auction.bids.first().value}")

            // require new bid to be higher than last
            if (value > auction.bids.first().value) {

                // return last bid to users liquidity.
                val lastBid = auction.bids.firstOrNull { it.owner == owner }
                if (lastBid != null) {
                    inventory.liquidity += lastBid.value
                }
                inventory.liquidity -= value

                val bids = auction.bids.toMutableList()
                bids.add(0, Bid(owner = owner, value = value))

                auction.bids = bids
            }
            auction
        })
    }

    // auction end sim = post value and item to inventory
    // auction end sim = update liquidity

    override fun alerts(): Flowable<Notification> {
        return flow(CompletableFuture.supplyAsync { Notification(message = "cool") })
    }
}