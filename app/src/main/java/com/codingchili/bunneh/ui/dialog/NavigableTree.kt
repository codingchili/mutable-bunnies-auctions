package com.codingchili.bunneh.ui.dialog

import com.codingchili.bunneh.R

open class NavigableTree(open val name: String, val query: String? = null, val icon: Int? = null) {
    val next = HashSet<NavigableTree>()
    var parent : NavigableTree? = null

    fun isLeaf(): Boolean {
        return next.isEmpty()
    }

    fun name(): String {
        return name
    }

    fun add(option: NavigableTree): NavigableTree {
        next.add(option)
        option.parent = this
        return this
    }
}

class Divider(override val name: String): NavigableTree(name = "divider") {
}

val auctionsFilterTree =
    listOf(
        NavigableTree(name = "Favorites", icon = R.drawable.icon_heart),
        Divider(name = "Selling"),
        NavigableTree(name = "Sold", icon = R.drawable.icon_auction_won),
        NavigableTree(name = "Active", icon = R.drawable.icon_active),
        NavigableTree(name = "Not Sold", icon = R.drawable.icon_not_sold),
        Divider(name = "Buying"),
        NavigableTree(name = "Won", icon = R.drawable.icon_auction_won),
        NavigableTree(name = "Leading", icon = R.drawable.icon_auction_leading),
        NavigableTree(name = "Overbid", icon = R.drawable.icon_auction_overbid),
        NavigableTree(name = "Lost", icon = R.drawable.icon_auction_lost)
    )

val searchFilterTree =
    listOf(
        NavigableTree(name = "Time left")
            .add(NavigableTree(name = "Ascending"))
            .add(NavigableTree(name = "Descending")),
        NavigableTree(name = "Name")
            .add(NavigableTree(name = "Ascending"))
            .add(NavigableTree(name = "Descending")),
        NavigableTree(name = "Rarity")
            .add(NavigableTree(name = "Ascending"))
            .add(NavigableTree(name = "Descending")),
        NavigableTree(name = "Current bid")
            .add(NavigableTree(name = "Ascending"))
            .add(NavigableTree(name = "Descending"))
    )

val serverRegionTree = listOf(
    NavigableTree(name = "BR"),
    NavigableTree(name = "EUNE"),
    NavigableTree(name = "LAN"),
    NavigableTree(name = "LAS"),
    NavigableTree(name = "NA"),
    NavigableTree(name = "OCE"),
    NavigableTree(name = "RU"),
    NavigableTree(name = "TR"),
    NavigableTree(name = "JPE"),
    NavigableTree(name = "EUW")
)

val navigableCategoryTree = listOf(
    NavigableTree(
        name = "Consumables",
        query = "type = 'consumable'"
    )
        .add(
            NavigableTree(
                name = "Leveling",
                query = "type = 'consumable' and title like 'apple'"
            )
        )
        .add(
            NavigableTree(
                name = "Food",
                query = "type = 'consumable' and title like 'apple'"
            )
        )
        .add(
            NavigableTree(
                name = "Potions",
                query = "type = 'consumable' and title like 'apple'"
            )
        ),
    NavigableTree(name = "Weapons")
        .add(NavigableTree(name = "Staff"))
        .add(NavigableTree(name = "Bow"))
        .add(NavigableTree(name = "Dagger"))
        .add(NavigableTree(name = "Hammer"))
        .add(NavigableTree(name = "Sword"))
        .add(NavigableTree(name = "Battleaxe")),
    NavigableTree(name = "Armors")
        .add(NavigableTree(name = "Plate"))
        .add(NavigableTree(name = "Leather"))
        .add(NavigableTree(name = "Cloth")),
    NavigableTree(name = "Slots")
        .add(NavigableTree(name = "Head"))
        .add(NavigableTree(name = "Chest"))
        .add(NavigableTree(name = "Legs"))
        .add(NavigableTree(name = "Weapon"))
        .add(NavigableTree(name = "Offhand"))
        .add(NavigableTree(name = "Ring"))
        .add(NavigableTree(name = "Neck"))
        .add(NavigableTree(name = "Cloak"))
        .add(NavigableTree(name = "Foot")),
    NavigableTree(name = "Rarity")
        .add(NavigableTree(name = "Mythic"))
        .add(NavigableTree(name = "Legendary"))
        .add(NavigableTree(name = "Epic"))
        .add(NavigableTree(name = "Rare"))
        .add(NavigableTree(name = "Uncommon"))
        .add(NavigableTree(name = "Common")),
    NavigableTree(name = "Quest")
)