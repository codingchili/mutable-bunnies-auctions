package com.codingchili.bunneh.api

import android.os.Handler
import android.os.Looper
import com.codingchili.bunneh.model.*
import com.codingchili.bunneh.ui.transform.formatValue
import io.reactivex.rxjava3.core.Single
import java.time.Instant
import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Mock implementation of the auction service.
 */
class LocalAuctionService : AuctionService {
    private var auctions = ArrayList<Auction>()
    private var notifications = HashMap<User, ArrayList<Notification>>()
    private var inventory = HashMap<User, Inventory>()
    private var authentication = AuthenticationService.instance
    private var favorites = HashMap<User, MutableSet<Auction>>()

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

    private fun userFavorites(): MutableSet<Auction> {
        return favorites.computeIfAbsent(authentication.user()!!) { HashSet<Auction>() }
    }

    private fun userNotifications(): MutableList<Notification> {
        return userNotifications(authentication.user()!!)
    }

    private fun userNotifications(user: User): MutableList<Notification> {
        return notifications.computeIfAbsent(user) { ArrayList() }
    }

    private fun userInventory(): Inventory {
        return userInventory(authentication.user()!!)
    }

    private fun userInventory(user: User): Inventory {
        return inventory.computeIfAbsent(user) {
            val funds = Random.nextInt(100_000..24_000_000)
            Inventory(
                funds = funds,
                liquidity = funds,
                items = listOf(
                    MockData.randomItem(),
                    MockData.randomItem(),
                    MockData.randomItem()
                )
            )
        }
    }

    override fun favorite(auction: Auction, add: Boolean): Single<Response> {
        return single(CompletableFuture.supplyAsync {
            Thread.sleep(MockData.delay)
            if (add) {
                userFavorites().add(auction)
            } else {
                userFavorites().remove(auction)
            }
            Response(success = true, message = "ok")
        })
    }

    override fun favorites(): Single<Set<Auction>> {
        return single<Set<Auction>>(CompletableFuture.supplyAsync {
            Thread.sleep(MockData.delay)
            userFavorites()
        })
    }

    override fun inventory(): Single<Inventory> {
        return single(CompletableFuture.supplyAsync {
            Thread.sleep(MockData.delay)
            userInventory()
        })
    }

    private fun handleAuctionEnd(auction: Auction) {
        val high = auction.bids.firstOrNull()
        val seller = userInventory(auction.seller)

        if (high != null) {
            val buyer = userInventory(high.owner)
            buyer.items = buyer.items.plus(auction.item)

            buyer.funds -= high.value
            seller.funds += high.value

            notify(
                auction.seller,
                auction,
                "<b>${auction.item.name}</b> was sold for <b>${formatValue(high.value)}</b>."
            )
            notify(
                high.owner,
                auction,
                "Won auction for <b>${auction.item.name}</b> at <b>${formatValue(high.value)}</b>"
            )

            // notify all other losing bidders.
            auction.bids.filterNot { it.owner == high.owner }
                .sortedByDescending { it.value }
                .distinctBy { it.owner }
                .forEach { bid ->
                    userInventory(bid.owner).liquidity += bid.value
                    notify(
                        bid.owner,
                        auction,
                        "Lost auction for <b>${auction.item.name}</b>, winning bid was <b>${formatValue(
                            high.value
                        )}</b>"
                    )
                }
        } else {
            seller.items = seller.items.plus(auction.item)
            notify(
                auction.seller,
                auction,
                "<b>${auction.item.name}</b> was not sold and returned to inventory."
            )
        }

        // notify all users that favorited the auction but did not place any bids.
        favorites.forEach { (user, favorites) ->
            if (auction in favorites && auction.bids.find { it.owner == user } == null) {
                notify(
                    user,
                    auction,
                    "Auction for <b>${auction.item.name}</b> finished at <b>${formatValue(high?.value ?: auction.initial)}</b>"
                )
            }
        }
    }

    private fun notify(user: User, auction: Auction, message: String) {
        userNotifications(user).add(
            Notification(
                icon = auction.item.icon,
                auctionId = auction.id,
                message = message
            )
        )
    }

    override fun auction(item: Item, value: Int): Single<Auction> {
        return single(CompletableFuture.supplyAsync {
            val seller = AuthenticationService.instance.user()!!
            val auction = Auction(item = item, initial = value, seller = seller)
            val inventory = userInventory()

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
            val inventory = userInventory()

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

    override fun notifications(): Single<List<Notification>> {
        return single<List<Notification>>(CompletableFuture.supplyAsync {
            Thread.sleep(MockData.delay)
            userNotifications()
        })
    }

    override fun findById(auctionId: String): Single<Auction> {
        return single(CompletableFuture.supplyAsync {
            auctions.find { it.id == auctionId } ?: throw Exception("Auction not found.")
        })
    }
}