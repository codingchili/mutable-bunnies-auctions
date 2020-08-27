package com.codingchili.bunneh.api

import android.util.Log
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.ItemRarity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class LocalAuctionService : AuctionService {
    override fun search(query: String): Flowable<List<Auction>> {
        return flow(CompletableFuture.supplyAsync {
            listOf(
                Auction(
                    bid = 12, item = Item(
                        icon = "flaming_stick.png",
                        rarity = ItemRarity.legendary,
                        quantity = 1,
                        name = "Fiery Stick +1",
                        slot = "weapon",
                        type = "staff"
                    )
                )
            )
        })
    }

    private fun flow(future: Future<List<Auction>>): Flowable<List<Auction>> {
        return Flowable.fromFuture(future)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun inventory() {
        TODO("Not yet implemented")
    }

    override fun auction() {
        TODO("Not yet implemented")
    }

    override fun bid() {
        TODO("Not yet implemented")
    }

}