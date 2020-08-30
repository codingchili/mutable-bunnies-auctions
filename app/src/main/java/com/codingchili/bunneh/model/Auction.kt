package com.codingchili.bunneh.model

import java.util.*
import kotlin.math.max

class Auction(
    val initial: Int = 0,
    val item: Item,
    val seller : User? = User("ethercat", "Ethercat"),
    val end: Long = Date().time + 60 * 1000,
    val id: String? = UUID.randomUUID().toString()
) {
    fun high(): Int {
        return max(initial, bids.firstOrNull()?.value ?: 0)
    }

    var bids = listOf<Bid>()
        /*listOf(
            Bid(
                owner = User("birthcake", "Birthcake"),
                value = 1295
            ),
            Bid(
                owner = User("ethercat", "Ethercat"),
                value = 1201
            ),
            Bid(
                owner = User("birthcake", "Birthcake"),
                value = 1200
            ),
            Bid(
                owner = User("ethercat", "Ethercat"),
                value = 1800
            ),
            Bid(
                owner = User("birthcake", "Birthcake"),
                value = 50
            )
        )*/
}