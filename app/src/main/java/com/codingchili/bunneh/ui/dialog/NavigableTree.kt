package com.codingchili.bunneh.ui.dialog

import com.codingchili.bunneh.R

open class NavigableTree(
    open val name: String,
    val resource: Int? = null
) {
    val next = HashSet<NavigableTree>()
    var parent: NavigableTree? = null

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

class Divider(override val name: String) : NavigableTree(name = "divider") {
}

val auctionsFilterTree =
    listOf(
        NavigableTree(name = "Favorites", resource = R.drawable.icon_star),
        Divider(name = "Selling"),
        NavigableTree(name = "Sold", resource = R.drawable.icon_auction_won),
        NavigableTree(name = "Active", resource = R.drawable.icon_active),
        NavigableTree(name = "Not Sold", resource = R.drawable.icon_not_sold),
        Divider(name = "Buying"),
        NavigableTree(name = "Won", resource = R.drawable.icon_auction_won),
        NavigableTree(name = "Leading", resource = R.drawable.icon_auction_leading),
        NavigableTree(name = "Overbid", resource = R.drawable.icon_auction_overbid),
        NavigableTree(name = "Lost", resource = R.drawable.icon_auction_lost)
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
    NavigableTree(name = "AF", resource = R.string.server_af),
    NavigableTree(name = "AN", resource = R.string.server_an),
    NavigableTree(name = "AS", resource = R.string.server_as),
    NavigableTree(name = "EU", resource = R.string.server_eu),
    NavigableTree(name = "NA", resource = R.string.server_na),
    NavigableTree(name = "OC", resource = R.string.server_oc),
    NavigableTree(name = "SA", resource = R.string.server_sa)
)

val navigableCategoryTree = listOf(
    NavigableTree(name = "Consumables")
        .add(NavigableTree(name = "Leveling"))
        .add(NavigableTree(name = "Food"))
        .add(NavigableTree(name = "Potions")),
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