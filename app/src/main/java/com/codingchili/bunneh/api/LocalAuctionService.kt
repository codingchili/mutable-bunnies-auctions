package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Mock implementation of the auction service.
 */
class LocalAuctionService : AuctionService {
    private var hardcodedItems: List<Item>
    private val hardcodedAuctions = ArrayList<Auction>()
    private var hardcodedInventory: Inventory

    init {
        hardcodedAuctions.add(
            Auction(
                bid = 825000,
                item = Item(
                    icon = "flaming_stick.png",
                    rarity = ItemRarity.legendary,
                    quantity = 1,
                    name = "Flaming Stick +4",
                    slot = "weapon",
                    type = "staff"
                )
            )
        )
        hardcodedAuctions.add(
            Auction(
                bid = 250,
                item = Item(
                    icon = "branch.png",
                    rarity = ItemRarity.common,
                    quantity = 1,
                    name = "Leafy Branch +1",
                    slot = "weapon",
                    type = "staff"
                )
            )
        )
        hardcodedAuctions.add(
            Auction(
                bid = 36000000,
                item = Item(
                    icon = "ring_1.png",
                    rarity = ItemRarity.mythic,
                    quantity = 1,
                    name = "The Sauring",
                    slot = "ring"
                )
            )
        )
        hardcodedAuctions.add(
            Auction(
                bid = 45,
                item = Item(
                    icon = "apple_green.png",
                    rarity = ItemRarity.rare,
                    quantity = 99,
                    name = "Green Apple",
                    type = "consumable"
                )
            )
        )
        hardcodedAuctions.add(
            Auction(
                bid = 11500,
                item = Item(
                    icon = "wand_1.png",
                    rarity = ItemRarity.rare,
                    quantity = 1,
                    name = "Spacewand +2",
                    slot = "weapon",
                    type = "staff"
                )
            )
        )
        hardcodedItems = hardcodedAuctions.map { it.item }
        hardcodedInventory =
            Inventory(items = hardcodedItems, liquidity = 7600000, funds = 17600000)
    }

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
                    hardcodedAuctions
                })
            }
        }
    }

    override fun inventory(): Flowable<Inventory> {
        return flow(CompletableFuture.supplyAsync { hardcodedInventory })
    }

    override fun auction(item: String, value: Int): Single<Response> {
        return single(CompletableFuture.supplyAsync { Response(message = "cool", success = true) })
    }

    override fun bid(value: Int, auctionId: String): Single<Response> {
        return single(CompletableFuture.supplyAsync { Response(message = "cool", success = true) })
    }

    override fun alerts(): Flowable<Notification> {
        return flow(CompletableFuture.supplyAsync { Notification(message = "cool") })
    }

    private fun <T> single(future: Future<T>): Single<T> {
        return Single.fromFuture(future)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun <T> flow(future: Future<T>): Flowable<T> {
        return Flowable.fromFuture(future)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }
}