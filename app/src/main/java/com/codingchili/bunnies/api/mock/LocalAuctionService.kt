package com.codingchili.bunnies.api.mock

import android.os.Handler
import android.os.Looper
import com.codingchili.banking.model.*
import com.codingchili.bunnies.api.AuctionService
import com.codingchili.bunnies.api.AuthenticationService
import com.codingchili.bunnies.api.protocol.ServerResponse
import com.codingchili.bunnies.model.single
import com.codingchili.bunnies.ui.transform.Type
import com.codingchili.bunnies.ui.transform.formatValue
import com.codingchili.core.protocol.ResponseStatus
import io.reactivex.rxjava3.core.Single
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Local mock implementation of the auction service.
 *
 * Supports multiple users and randomly generates a starting
 * inventory for testing. The in-memory database is reset
 * when the application is restarted and all searches returns
 * the same hits.
 *
 * Error handling can be tested by searching for "error" or "null".
 * Most calls to the service are delayed by a time configured in the
 * MockData class. This allows testing the app under slower network
 * conditions to make sure user feedback is available. It also
 * ensures that no service calls are blocking the ui.
 */
class LocalAuctionService : AuctionService {
    private var notifications = HashMap<String, ArrayList<Notification>>()
    private var inventory = HashMap<String, Inventory>()
    private var favorites = HashMap<String, MutableSet<Auction>>()
    private var auctions = ArrayList<Auction>()
    private var authentication =
        AuthenticationService.instance

    override fun search(query: QueryType, param: String?): Single<List<Auction>> {
        val user = authentication.user()!!
        val fiveMinutes = 300
        val finished = auctions.filter { it.finished() }
        val active = auctions.filter { !it.finished() }
        return single(CompletableFuture.supplyAsync {
            when (query) {
                QueryType.favorites -> auctions.filter { favorites[user]?.contains(it) ?: false }
                QueryType.sold -> finished.filter { it.seller == user && it.bids.isNotEmpty() }
                QueryType.active -> active.filter { it.seller == user }
                QueryType.not_sold -> finished.filter { it.seller == user && it.bids.isEmpty() }
                QueryType.won -> finished.filter { it.bids.isNotEmpty() && it.bids[0].owner == user }
                QueryType.lost -> finished.filter { it.bids.find { it.owner == user } != null }
                QueryType.leading -> active.filter { it.bids.take(1).any { it.owner == user } }
                QueryType.overbid -> active.filter { it.bids.drop(1).any { it.owner == user } }
                QueryType.armor_type -> active.filter { it.item.type == param!! }
                QueryType.weapon_type -> active.filter { it.item.type == param!! }
                QueryType.ending_soon -> active.filter {
                    TimeUnit.MILLISECONDS.toSeconds(
                        Instant.now().toEpochMilli() - it.end
                    ) < fiveMinutes
                }
                QueryType.quest -> active.filter { it.item.type == Type.quest }
                QueryType.text -> active.filter {
                    it.item.name.toLowerCase().contains(param!!)
                            || it.item.description.toLowerCase().contains(param)
                }
                QueryType.slot -> active.filter { it.item.slot == param }
                QueryType.consumable -> active.filter { it.item.type == Type.consumable }
                QueryType.rarity -> active.filter { it.item.rarity == ItemRarity.valueOf(param!!) }
                QueryType.seller -> auctions.filter { it.seller == param }
            }
        })
    }

    private fun userFavorites(): MutableSet<Auction> {
        return favorites.computeIfAbsent(authentication.user()!!) { HashSet<Auction>() }
    }

    private fun userNotifications(): MutableList<Notification> {
        return userNotifications(authentication.user()!!)
    }

    private fun userNotifications(user: String): MutableList<Notification> {
        return notifications.computeIfAbsent(user) { ArrayList() }
    }

    private fun userInventory(): Inventory {
        return userInventory(authentication.user()!!)
    }

    private fun userInventory(user: String): Inventory {
        return inventory.computeIfAbsent(user) {
            val funds = Random.nextInt(64_000..32_000_000)
            Inventory(
                funds = funds,
                liquidity = funds,
                items = listOf(
                    // generate some random items to start with.
                    MockData.randomItem(),
                    MockData.randomItem(),
                    MockData.randomItem(),
                    MockData.randomItem(),
                    MockData.randomItem(),
                    MockData.randomItem()
                )
            )
        }
    }

    override fun favorite(auction: Auction, add: Boolean): Single<ServerResponse> {
        return single(CompletableFuture.supplyAsync {
            Thread.sleep(MockData.delay)
            if (add) {
                userFavorites().add(auction)
            } else {
                userFavorites().remove(auction)
            }
            ServerResponse(
                status = ResponseStatus.ACCEPTED,
                message = "ok"
            )
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

    private fun notify(user: String, auction: Auction, message: String) {
        userNotifications(user).add(
            0,
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

            // verify that the item exists in inventory.
            val items = inventory.items.toMutableList()
            if (items.remove(item)) {
                inventory.items = items
                auctions.add(auction)
            } else {
                throw Exception("No longer in possession of the given item.")
            }

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

                // ensure that current liquidity is enough to place the bid
                if (inventory.liquidity > value) {
                    inventory.liquidity -= value
                    val bids = auction.bids.toMutableList()
                    val lastBid = auction.bids.firstOrNull()

                    // add the new bid to the auction.
                    bids.add(0, Bid(owner = owner, value = value))
                    auction.bids = bids

                    // return the value of the last bid to its owners liquidity.
                    if (lastBid != null) {
                        userInventory(lastBid.owner).liquidity += lastBid.value
                    }

                    // notify the previous high bidder.
                    val message =
                        "Outbid by <b>${owner}</b> on auction for <b>${auction.item.name}</b>, new bid <b>${formatValue(
                            value
                        )}</b>"
                    auction.bids.filterNot { it.owner == owner }
                        .take(1)
                        .forEach {
                            notify(
                                it.owner,
                                auction,
                                message
                            )
                        }
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