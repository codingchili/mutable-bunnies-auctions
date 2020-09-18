package com.codingchili.bunnies.ui.links

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.codingchili.banking.model.Auction

class AuctionListViewModel(private val state: SavedStateHandle) : ViewModel() {
    var list: MutableLiveData<List<Auction>> = state.getLiveData(KEY_LIST, listOf())

    companion object {
        private const val KEY_LIST = "list"
    }

    fun setList(auctions: List<Auction>) {
        this.list.value = auctions
        state.set(KEY_LIST, auctions)
    }

    val getList: LiveData<List<Auction>>
        get() = list
}