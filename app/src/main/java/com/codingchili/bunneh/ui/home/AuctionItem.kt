package com.codingchili.bunneh.ui.home

import java.util.*
import kotlin.collections.ArrayList

class AuctionItem(
    val icon: String,
    val quantity: Int,
    val rarity: ItemRarity,
    val title: String,
    val bid: Int,
    val description: String = "This is a leafy branch, the leaves are still green yet not for long. It feels like it could easily snap, yet there's an unnatural tingle in your fingers when touched. This branch might have been exposed to second hand magic use, its effects are unclear.",
    val stats: String = "+4 Constitution\n+1 Dexterity\n+1 Wisdom\n+4% Magic resist",
    val slot: String? = null,
    val type: String? = null
) {
    val seller = "EU/Hazel Dream/Ethercat"
    val bids =
        listOf(
            AuctionBid(owner = "EU/Gloomville/Dr.Foo", value = 41),
            AuctionBid(owner = "EU/Hazel Dream/Birthcake", value = 1200),
            AuctionBid(owner = "EU/Angel Oak/Etherbloom", value = 1201),
            AuctionBid(owner = "EU/Angel Oak/Foowoo", value = 1800),
            AuctionBid(owner = "EU/Hazel Dream/Birthcake", value = 36000000)
        )
    val end: Long = Date().time + 72 * 60 * 60 * 1000 // 72 hours
}