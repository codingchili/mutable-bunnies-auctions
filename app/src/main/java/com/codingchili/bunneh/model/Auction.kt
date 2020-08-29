package com.codingchili.bunneh.model

import java.util.*

class Auction(
    val bid: Int,
    val item: Item
) {
    val id = 0
    val seller = User("y", "Ethercat")
    val bids =
        listOf(
            Bid(
                owner = User("x", "Birthcake"),
                value = 41
            ),
            Bid(
                owner = User("y", "Ethercat"),
                value = 1200
            ),
            Bid(
                owner = User("x", "Birthcake"),
                value = 1201
            ),
            Bid(
                owner = User("y", "Ethercat"),
                value = 1800
            ),
            Bid(
                owner = User("x", "Birthcake"),
                value = 36000000
            )
        )
    val end: Long = Date().time + 60 * 1000 // 1 minute
}