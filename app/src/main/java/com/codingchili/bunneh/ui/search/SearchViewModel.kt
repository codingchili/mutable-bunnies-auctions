package com.codingchili.bunneh.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingchili.bunneh.model.Auction

class SearchViewModel: ViewModel() {
    var auctions: MutableLiveData<List<Auction>> = MutableLiveData()

    fun setAuctionList(auctions:List<Auction>){
        this.auctions.value = auctions
    }

    val getAuctionList: LiveData<List<Auction>>
        get() = auctions

}