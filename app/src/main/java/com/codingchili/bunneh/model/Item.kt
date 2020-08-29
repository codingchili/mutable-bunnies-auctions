package com.codingchili.bunneh.model

class Item(
    val icon: String,
    val quantity: Int,
    val rarity: ItemRarity,
    val name: String,
    val description: String = "This is a leafy branch, the leaves are still green yet not for long. It feels like it could easily snap, yet there's an unnatural tingle in your fingers when touched. This branch might have been exposed to second hand magic use, its effects are unclear.",
    val stats: Stats = Stats(),
    val slot: String? = null,
    val type: String? = null
) {
    lateinit var id: String
}