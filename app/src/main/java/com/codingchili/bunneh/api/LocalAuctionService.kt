package com.codingchili.bunneh.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.codingchili.bunneh.model.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import java.time.Instant
import java.util.concurrent.CompletableFuture
import kotlin.math.max

/**
 * Mock implementation of the auction service.
 */
class LocalAuctionService : AuctionService {
    private var auctions = ArrayList<Auction>()
    private lateinit var inventory: Inventory

    init {
        val authentication = AuthenticationService.instance
        val user = authentication.user()

        // temporary authentication as another user to mock some auctions.
        authentication.authenticate("Dr.Meow", "").subscribe { response, error ->
            resetInventory()
            MockData.auctions.forEach { auction -> auction(auction.item, auction.initial) }

            // switch back to original user.
            Handler(Looper.getMainLooper()).postDelayed({
                authentication.authenticate(user!!.name, "").subscribe { _, _ ->
                    resetInventory()
                }
            }, 500)
        }
    }

    private fun resetInventory() {
        inventory = Inventory(
            items = listOf(MockData.branch, MockData.flamingStick, MockData.spacewand),
            liquidity = 7600000,
            funds = 7600000
        )
    }

    override fun search(query: String): Single<List<Auction>> {
        return when (query) {
            "null" -> {
                single(CompletableFuture.supplyAsync { listOf<Auction>() })
            }
            "error" -> {
                val future = CompletableFuture<List<Auction>>()
                future.completeExceptionally(Exception("Failed to parse search query."))
                single<List<Auction>>(future)
            }
            else -> {
                single<List<Auction>>(CompletableFuture.supplyAsync {
                    Thread.sleep(MockData.delay)
                    auctions.filter { it.end > Instant.now().toEpochMilli() }
                })
            }
        }
    }

    override fun inventory(): Flowable<Inventory> {
        return flow(CompletableFuture.supplyAsync { inventory })
    }

    private fun handleAuctionEnd(auction: Auction) {
        val currentUser = AuthenticationService.instance.user()
        val high = auction.bids.firstOrNull()

        // mock only active user
        if (currentUser == auction.seller || high?.owner == currentUser) {
            // return the item to inventory if currentUser won the auction or is author of high bid.
            inventory.items = inventory.items.plus(auction.item)

            if (high != null) {
                // currentUser has won another users auction, remove funds to balance liquidity.
                inventory.funds -= high.value
            }
        }
    }

    override fun auction(item: Item, value: Int): Single<Auction> {
        return single(CompletableFuture.supplyAsync {
            val seller = AuthenticationService.instance.user()
            val auction = Auction(item = item, initial = value, seller = seller)

            Handler(Looper.getMainLooper()).postDelayed({
                handleAuctionEnd(auction)
            }, auction.end - Instant.now().toEpochMilli())

            // verification that item is in possession is done on the server.
            inventory.items = inventory.items.filterNot { it == item }
            auctions.add(auction)

            auction
        })
    }

    override fun bid(value: Int, auction: Auction): Single<Auction> {
        return single(CompletableFuture.supplyAsync {
            val owner = AuthenticationService.instance.user()!!
            val highestBid = auction.bids.firstOrNull()?.value ?: 0

            if (owner == auction.seller) {
                throw Exception("Cannot bid on own auction.")
            }

            // require new bid to be higher than last
            if (value > max(auction.initial, highestBid)) {

                // return last bid to users liquidity.
                val lastBid = auction.bids.firstOrNull { it.owner == owner }
                if (lastBid != null) {
                    inventory.liquidity += lastBid.value
                }

                if (inventory.liquidity > value) {
                    inventory.liquidity -= value
                    val bids = auction.bids.toMutableList()
                    bids.add(0, Bid(owner = owner, value = value))
                    auction.bids = bids
                } else {
                    throw Exception("Funds are not enough to increase bid.")
                }
            } else {
                throw Exception("Bid is not high enough.")
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