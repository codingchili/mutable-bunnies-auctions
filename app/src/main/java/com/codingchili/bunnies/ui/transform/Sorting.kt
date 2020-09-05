package com.codingchili.bunnies.ui.transform

import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.Item
import com.codingchili.bunnies.R

/**
 * Implements sorting, up and down of auctions and items based on a few properties.
 */
class Sorter {
    var ascending = false
    private var method = this::sortByBid

    /**
     * Sets the sorting method by its resource id.
     */
    fun setMethodByName(name: Int) {
        method = when (name) {
            R.string.nav_current_bid -> this::sortByBid
            R.string.nav_name -> this::sortByName
            R.string.nav_time_left -> this::sortByEnd
            R.string.nav_rarity -> this::sortByRarity
            else -> this::sortByRarity
        }
    }

    /**
     * Applies the current sorting algorithm on the given list and returns a new sorted list.
     */
    fun sortItems(list: List<Item>): List<Item> {
        // wrap in auction to support sort code reuse, after sort unwrap.
        return sort(list.map {
            Auction(initial = 0, item = it, seller = "")
        }).map {
            it.item
        }
    }

    /**
     * Applies the current sorting algorithm on the given list and returns a new sorted list.
     */
    fun sort(list: List<Auction>): List<Auction> {
        return method.invoke(ascending, list)
    }

    private fun sortByBid(ascending: Boolean, list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.initial } else list.sortedByDescending { it.initial }
    }

    private fun sortByName(ascending: Boolean, list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.item.name } else list.sortedByDescending { it.item.name }
    }

    private fun sortByEnd(ascending: Boolean, list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.end } else list.sortedByDescending { it.end }
    }

    private fun sortByRarity(ascending: Boolean, list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.item.rarity } else list.sortedByDescending { it.item.rarity }
    }
}