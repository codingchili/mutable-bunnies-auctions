package com.codingchili.bunneh.ui.search

import java.time.Instant

class AuctionBid(
    val owner: String,
    val value: Int,
    val date: Long = Instant.now().toEpochMilli()
) {
}