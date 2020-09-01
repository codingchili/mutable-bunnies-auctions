package com.codingchili.bunneh.model

import java.time.Instant
import java.util.*
import kotlin.math.max

class Auction(
    val initial: Int = 0,
    val item: Item,
    val seller : User,
    val end: Long = Date().time + 2 * 60 * 1000, // 2 minutes for test.
    val id: String? = UUID.randomUUID().toString()
) {
    fun high(): Int {
        return max(initial, bids.firstOrNull()?.value ?: 0)
    }

    fun finished(): Boolean {
        return Instant.now().toEpochMilli() > end
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