package com.codingchili.bunnies.ui.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.codingchili.banking.model.Item

class ItemViewModel(private val state : SavedStateHandle) : ViewModel() {
    var item: MutableLiveData<Item> = state.getLiveData(KEY_ITEM)

    companion object {
        private const val KEY_ITEM = "item"
    }

    fun setItem(item: Item){
        this.item.value = item
        state.set(KEY_ITEM, item)
    }

    val getItem: LiveData<Item>
        get() = item

}