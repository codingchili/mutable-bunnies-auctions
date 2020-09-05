package com.codingchili.bunnies.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingchili.banking.model.Auction

/**
 * This viewmodels retains state for the search fragment, so that hits does not
 * disappear when navigating using the bottom navigation bar.
 */
class SearchViewModel: ViewModel() {
    var auctions: MutableLiveData<List<Auction>> = MutableLiveData()

    fun setAuctionList(auctions:List<Auction>){
        this.auctions.value = auctions
    }

    val getAuctionList: LiveData<List<Auction>>
        get() = auctions

}