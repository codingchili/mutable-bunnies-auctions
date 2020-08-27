package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Auction
import io.reactivex.rxjava3.core.Flowable


interface AuctionService {
    fun search(query: String): Flowable<List<Auction>>
    fun inventory()
    fun auction()
    fun bid()
}