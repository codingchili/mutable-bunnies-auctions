package com.codingchili.bunnies.ui.dialog

import com.codingchili.banking.model.ItemRarity
import com.codingchili.banking.model.QueryType
import com.codingchili.bunnies.R

/**
 * The navigable tree class builds a tree of resources which can be
 * navigated and listened to inside a NavigableTreeDialog.
 */
open class NavigableTree(
    open val name: Int,
    val resource: Int? = null,
    val query: QueryType? = null,
    val param: String? = null
) {
    val next = HashSet<NavigableTree>()
    var parent: NavigableTree? = null

    /**
     * @return true if this node does not have any children.
     */
    fun isLeaf(): Boolean {
        return next.isEmpty()
    }

    /**
     * @return the query type assigned to this node or its immediate parent.
     * queries can be navigated through and sorted by type, where each leaf
     * has a different query param but shared type.
     */
    fun query(): QueryType {
        if (query == null) {
            if (parent != null) {
                return parent?.query!!
            }
        }
        return query!!
    }

    /**
     * The name of the navigable node, this is the id of the string resource
     * for its display name and it can also be used for comparison.
     */
    fun name(): Int {
        return name
    }

    /**
     * @param option a new child node to add to this tree.
     * @return fluent
     */
    fun add(option: NavigableTree): NavigableTree {
        next.add(option)
        option.parent = this
        return this
    }
}

/**
 * The divider is a placeholder that can be used to group child nodes more clearly.
 * The divider should not accept user input.
 */
class Divider(override val name: Int) : NavigableTree(name = R.string.divider) {
}

/**
 * This tree is used to select a category of auctions to show.
 */
val auctionsFilterTree =
    listOf(
        NavigableTree(
            name = R.string.nav_favorites,
            resource = R.drawable.icon_star,
            query = QueryType.favorites
        ),
        Divider(name = R.string.nav_selling),
        NavigableTree(
            name = R.string.nav_sold,
            resource = R.drawable.icon_auction_won,
            query = QueryType.sold
        ),
        NavigableTree(
            name = R.string.nav_active,
            resource = R.drawable.icon_active,
            query = QueryType.active
        ),
        NavigableTree(
            name = R.string.nav_not_sold,
            resource = R.drawable.icon_not_sold,
            query = QueryType.not_sold
        ),
        Divider(name = R.string.nav_buying),
        NavigableTree(
            name = R.string.nav_won,
            resource = R.drawable.icon_auction_won,
            query = QueryType.won
        ),
        NavigableTree(
            name = R.string.nav_leading,
            resource = R.drawable.icon_auction_leading,
            query = QueryType.leading
        ),
        NavigableTree(
            name = R.string.nav_overbid,
            resource = R.drawable.icon_auction_overbid,
            query = QueryType.overbid
        ),
        NavigableTree(
            name = R.string.nav_lost,
            resource = R.drawable.icon_auction_lost,
            query = QueryType.lost
        )
    )

/**
 * This tree is used to sort items in the inventory.
 */
val sortItemsTree =
    listOf(
        NavigableTree(name = R.string.nav_name)
            .add(NavigableTree(name = R.string.nav_ascending))
            .add(NavigableTree(name = R.string.nav_descending)),
        NavigableTree(name = R.string.nav_rarity)
            .add(NavigableTree(name = R.string.nav_ascending))
            .add(NavigableTree(name = R.string.nav_descending))
    )

/**
 * This tree is used to sort auctions in the auctions fragment.
 */
val sortAuctionsTree =
    listOf(
        NavigableTree(name = R.string.nav_time_left)
            .add(NavigableTree(name = R.string.nav_ascending))
            .add(NavigableTree(name = R.string.nav_descending)),
        NavigableTree(name = R.string.nav_name)
            .add(NavigableTree(name = R.string.nav_ascending))
            .add(NavigableTree(name = R.string.nav_descending)),
        NavigableTree(name = R.string.nav_rarity)
            .add(NavigableTree(name = R.string.nav_ascending))
            .add(NavigableTree(name = R.string.nav_descending)),
        NavigableTree(name = R.string.nav_current_bid)
            .add(NavigableTree(name = R.string.nav_ascending))
            .add(NavigableTree(name = R.string.nav_descending))
    )

/**
 * This tree is used to manually select the server region to connect to in the login fragment.
 */
val serverRegionTree = listOf(
    NavigableTree(name = R.string.region_af, resource = R.string.server_af),
    NavigableTree(name = R.string.region_an, resource = R.string.server_an),
    NavigableTree(name = R.string.region_as, resource = R.string.server_as),
    NavigableTree(name = R.string.region_eu, resource = R.string.server_eu),
    NavigableTree(name = R.string.region_na, resource = R.string.server_na),
    NavigableTree(name = R.string.region_oc, resource = R.string.server_oc),
    NavigableTree(name = R.string.region_sa, resource = R.string.server_sa)
)

/**
 * This tree is used to perform quick searches based on categories.
 */
val navigableCategoryTree = listOf(
    NavigableTree(name = R.string.consumables, query = QueryType.consumable)
        .add(NavigableTree(name = R.string.leveling, param = "leveling"))
        .add(NavigableTree(name = R.string.food, param = "food"))
        .add(NavigableTree(name = R.string.potions, param = "potion")),
    NavigableTree(name = R.string.weapons, query = QueryType.weapon_type)
        .add(NavigableTree(name = R.string.staff, param = "staff"))
        .add(NavigableTree(name = R.string.bow, param = "bow"))
        .add(NavigableTree(name = R.string.dagger, param = "dagger"))
        .add(NavigableTree(name = R.string.hammer, param = "hammer"))
        .add(NavigableTree(name = R.string.sword, param = "sword"))
        .add(NavigableTree(name = R.string.battleaxe, param = "battleaxe")),
    NavigableTree(name = R.string.armors, query = QueryType.armor_type)
        .add(NavigableTree(name = R.string.plate, param = "plate"))
        .add(NavigableTree(name = R.string.leather, param = "leather"))
        .add(NavigableTree(name = R.string.cloth, param = "cloth")),
    NavigableTree(name = R.string.slots, query = QueryType.slot)
        .add(NavigableTree(name = R.string.head, param = "head"))
        .add(NavigableTree(name = R.string.chest, param = "chest"))
        .add(NavigableTree(name = R.string.legs, param = "legs"))
        .add(NavigableTree(name = R.string.weapon, param = "weapon"))
        .add(NavigableTree(name = R.string.offhand, param = "offhand"))
        .add(NavigableTree(name = R.string.ring, param = "ring"))
        .add(NavigableTree(name = R.string.neck, param = "neck"))
        .add(NavigableTree(name = R.string.cloak, param = "cloak"))
        .add(NavigableTree(name = R.string.foot, param = "foot")),
    NavigableTree(name = R.string.nav_rarity, query = QueryType.rarity)
        .add(NavigableTree(name = R.string.mythic, param = ItemRarity.mythic.name))
        .add(NavigableTree(name = R.string.legendary, param = ItemRarity.legendary.name))
        .add(NavigableTree(name = R.string.epic, param = ItemRarity.epic.name))
        .add(NavigableTree(name = R.string.rare, param = ItemRarity.rare.name))
        .add(NavigableTree(name = R.string.uncommon, param = ItemRarity.uncommon.name))
        .add(NavigableTree(name = R.string.common, param = ItemRarity.common.name)),
    NavigableTree(name = R.string.quest, query = QueryType.quest)
)