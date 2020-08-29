package com.codingchili.bunneh.model

import java.time.Instant

class Bid(
    val owner: User,
    val value: Int,
    val date: Long = Instant.now().toEpochMilli()
) {
}