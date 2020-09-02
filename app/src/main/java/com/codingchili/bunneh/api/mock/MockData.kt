package com.codingchili.bunneh.api.mock

import com.codingchili.banking.model.Item
import com.codingchili.banking.model.ItemRarity
import com.codingchili.bunneh.ui.transform.Type

class MockData {
    companion object {
        const val delay = 950L
        val spacewand = Item(
            icon = "wand_1.png",
            rarity = ItemRarity.rare,
            quantity = 1,
            name = "Spacewand +2",
            slot = Type.weapon,
            type = "staff",
            description = "Rumors have it that the spacewand is actually from space. Maybe there's actually alien wizards out there."
        )

        val greenApple = Item(
            icon = "apple_green.png",
            rarity = ItemRarity.rare,
            quantity = 99,
            name = "Green Apple",
            slot = Type.consumable,
            description = "Its just a green apple. A deliciously green, fresh and juicy apple. MMmmmmm."
        )

        val flamingStick = Item(
            icon = "flaming_stick.png",
            rarity = ItemRarity.legendary,
            quantity = 1,
            name = "Flaming Stick +4",
            slot = Type.weapon,
            type = "staff",
            description = "Its a stick. With fire. A Flaming stick. Slightly more dangerous than a regular stick. It is said that the flame lasts forever and clings onto anything that touches it."
        )

        val sauring = Item(
            icon = "ring_1.png",
            rarity = ItemRarity.mythic,
            quantity = 1,
            name = "The Sauring",
            slot = "ring",
            type = Type.armor,
            description = "The mythic ring that was once worn by Sauron himself. What the ring does is left a mystery, until it sits firmly on your finger."
        )

        val branch = Item(
            icon = "branch.png",
            rarity = ItemRarity.common,
            quantity = 1,
            name = "Leafy Branch +1",
            slot = "weapon",
            type = "staff",
            description = "Its just a regular branch. Without the fire. Green leaves, wow wow."
        )

        fun randomItem(): Item {
            return listOf(
                branch,
                sauring,
                spacewand,
                flamingStick,
                greenApple
            ).random()
        }
    }
}