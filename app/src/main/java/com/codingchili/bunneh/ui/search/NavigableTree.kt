package com.codingchili.bunneh.ui.search

class NavigableTree(val name: String, val query: String? = null) {
    val next = HashSet<NavigableTree>()

    fun isLeaf(): Boolean {
        return next.isEmpty()
    }

    fun name(): String {
        return name
    }

    fun add(option: NavigableTree): NavigableTree {
        next.add(option)
        return this
    }
}

public val searchFilterTree =
    listOf(
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

public val navigableCategoryTree = listOf(
    NavigableTree(name = "Consumables", query = "type = 'consumable'")
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