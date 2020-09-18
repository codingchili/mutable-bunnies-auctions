package com.codingchili.bunnies.ui.auction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.codingchili.banking.model.Auction

class AuctionViewModel(private val state: SavedStateHandle) : ViewModel() {
    var list: MutableLiveData<List<Auction>> = state.getLiveData(KEY_LIST, listOf())
    var auction: MutableLiveData<Auction> = state.getLiveData(KEY_AUCTION)

    companion object {
        private const val KEY_LIST = "related"
        private const val KEY_AUCTION = "auction"
    }

    fun setAuction(auction: Auction) {
        this.auction.value = auction
        state.set(KEY_AUCTION, auction)
    }

    val getAuction: LiveData<Auction>
        get() = auction

    fun setList(auctions: List<Auction>) {
        this.list.value = auctions
        state.set(KEY_LIST, auctions)
    }

    val getList: LiveData<List<Auction>>
        get() = list
}