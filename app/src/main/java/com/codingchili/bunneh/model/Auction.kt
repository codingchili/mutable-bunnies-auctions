package com.codingchili.bunneh.model

import java.util.*

class Auction(
    val bid: Int,
    val item: Item
) {
    val id = 0
    val seller = "Hazel Dream/Ethercat"
    val bids =
        listOf(
            Bid(
                owner = "Gloomville/Dr.Foo",
                value = 41
            ),
            Bid(
                owner = "Hazel Dream/Birthcake",
                value = 1200
            ),
            Bid(
                owner = "Angel Oak/Etherbloom",
                value = 1201
            ),
            Bid(
                owner = "Angel Oak/Foowoo",
                value = 1800
            ),
            Bid(
                owner = "Hazel Dream/Birthcake",
                value = 36000000
            )
        )
    val end: Long = Date().time + 60 * 60 * 1000 // 1 hour
}