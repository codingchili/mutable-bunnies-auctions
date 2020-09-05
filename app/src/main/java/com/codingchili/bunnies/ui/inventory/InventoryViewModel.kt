package com.codingchili.bunnies.ui.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingchili.banking.model.Inventory

/**
 * View model to preserve the users inventory across orientation and layout changes.
 * The viewmodel needs to be disposed by the activity when the user logs out
 * to prevent leaking information to other users.
 *
 * The viewmodel is updated in the background when the inventory fragment is shown.
 */
class InventoryViewModel : ViewModel() {
    var inventory: MutableLiveData<Inventory> = MutableLiveData(Inventory())

    fun setInventory(inventory: Inventory) {
        this.inventory.value = inventory
    }

    val getInventory: LiveData<Inventory>
        get() = inventory
}