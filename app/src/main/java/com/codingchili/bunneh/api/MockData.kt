package com.codingchili.bunneh.api

import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.ItemRarity
import com.codingchili.bunneh.model.User
import com.codingchili.bunneh.ui.transform.Type

class MockData {
    companion object {
        const val delay = 950L
        val auctions = ArrayList<Auction>()
        val spacewand = Item(
            icon = "wand_1.png",
            rarity = ItemRarity.rare,
            quantity = 1,
            name = "Spacewand +2",
            slot = Type.weapon,
            type = "staff"
        )

        val greenApple = Item(
            icon = "apple_green.png",
            rarity = ItemRarity.rare,
            quantity = 99,
            name = "Green Apple",
            slot = Type.consumable
        )

        val flamingStick = Item(
            icon = "flaming_stick.png",
            rarity = ItemRarity.legendary,
            quantity = 1,
            name = "Flaming Stick +4",
            slot = Type.weapon,
            type = "staff"
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
            type = "staff"
        )

        fun randomItem(): Item {
            return listOf(branch, sauring, spacewand, flamingStick, greenApple).random()
        }

        val seller = User("admin", "admin")

        init {
            auctions.addAll(
                listOf(
                    Auction(id = "1", initial = 25, item = flamingStick, seller = seller),
                    Auction(id = "2", initial = 25, item = branch, seller = seller),
                    Auction(id = "3", initial = 25, item = sauring, seller = seller),
                    Auction(id = "4", initial = 25, item = greenApple, seller = seller),
                    Auction(id = "5", initial = 25, item = spacewand, seller = seller)
                )
            )
        }
    }
}