package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.ItemRarity
import com.codingchili.bunneh.ui.transform.Type

class MockData {
    companion object {
        const val delay = 1200L
        val auctions = ArrayList<Auction>()
        val spacewand = Item(
            icon = "wand_1.png",
            rarity = ItemRarity.rare,
            quantity = 1,
            name = "Spacewand +2",
            slot = "weapon",
            type = Type.weapon
        )

        val greenApple = Item(
            icon = "apple_green.png",
            rarity = ItemRarity.rare,
            quantity = 99,
            name = "Green Apple",
            type = Type.consumable
        )

        val flamingStick = Item(
            icon = "flaming_stick.png",
            rarity = ItemRarity.legendary,
            quantity = 1,
            name = "Flaming Stick +4",
            slot = "weapon",
            type = Type.weapon
        )

        val sauring = Item(
            icon = "ring_1.png",
            rarity = ItemRarity.mythic,
            quantity = 1,
            name = "The Sauring",
            slot = "ring",
            type = Type.armor
        )

        val branch = Item(
            icon = "branch.png",
            rarity = ItemRarity.common,
            quantity = 1,
            name = "Leafy Branch +1",
            slot = "weapon",
            type = Type.weapon
        )

        init {
            auctions.addAll(
                listOf(
                    Auction(initial = 25, item = flamingStick),
                    Auction(initial = 25, item = branch),
                    Auction(initial = 25, item = sauring),
                    Auction(initial = 25, item = greenApple),
                    Auction(initial = 25, item = spacewand)
                )
            )
        }
    }
}