package com.codingchili.bunneh.ui.transform

import android.content.Context
import com.codingchili.bunneh.R
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item

class Sorter {
    var ascending = false
    private var method = this::sortByBid

    fun setMethodByName(context: Context, name: String) {
        when (name) {
            context.getString(R.string.sort_bid) -> method = this::sortByBid
            context.getString(R.string.sort_name) -> method = this::sortByName
            context.getString(R.string.sort_end) -> method = this::sortByEnd
            context.getString(R.string.sort_rarity) -> method = this::sortByRarity
        }
    }

    fun sortItems(list: List<Item>): List<Item> {
        // wrap in auction to support sort code reuse, after sort unwrap.
        return sort(list.map {
            Auction(bid = 0, item = it)
        }).map {
            it.item
        }
    }

    fun sort(list: List<Auction>): List<Auction> {
        return method.invoke(ascending, list)
    }

    fun sortByBid(ascending: Boolean, list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.bid } else list.sortedByDescending { it.bid }
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