package com.codingchili.bunnies.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.codingchili.bunnies.R
import com.codingchili.bunnies.api.AuctionService
import com.codingchili.bunnies.ui.AppToast
import com.codingchili.bunnies.ui.auction.AuctionFragment
import com.codingchili.bunnies.ui.auction.AuctionViewModel
import com.codingchili.bunnies.ui.dialog.Dialogs
import com.codingchili.bunnies.ui.dialog.NumberInputDialog
import com.codingchili.bunnies.ui.transform.ServerResource
import com.codingchili.bunnies.ui.transform.Type
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.function.Consumer

/**
 * This fragment displays an item from the users inventory.
 * From here the user can sell the item and view details about the item.
 */
class ItemFragment : Fragment() {
    private val model by activityViewModels<ItemViewModel>()
    private val shared by activityViewModels<AuctionViewModel>()
    private var service = AuctionService.instance

    companion object {
        val TAG = "item.fragment"
    }

    private fun auctionHandler(view: View): Consumer<Int> {
        return Consumer<Int> { initialValue ->
            service.auction(this.model.item.value!!, initialValue).bindToLifecycle(view).subscribe { response, e ->
                if (e == null) {
                    this.shared.auction.value = response
                    // empty the related hits.
                    this.shared.list.value = listOf()

                    requireActivity().supportFragmentManager.popBackStack(
                        ItemFragment.TAG,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.root, AuctionFragment())
                        .addToBackStack(AuctionFragment.TAG)
                        .commit()
                } else {
                    AppToast.show(context, e.message)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_item, container, false)
        val item = this.model.item.value!!

        requireActivity().title = item.name
        fragment.findViewById<TextView>(R.id.item_description).text = item.description
        fragment.findViewById<TextView>(R.id.item_stats).text = item.stats.toString()

        fragment.findViewById<MaterialButton>(R.id.button_sell).setOnClickListener {
            NumberInputDialog(auctionHandler(fragment))
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        fragment.findViewById<FloatingActionButton>(R.id.back)
            .setOnClickListener { requireActivity().onBackPressed() }

        updateQuantity(fragment)
        ServerResource.icon(fragment.findViewById(R.id.item_image), item.icon)
        Type.updateLabels(fragment, item)

        return fragment
    }

    private fun updateQuantity(fragment: View) {
        val item = this.model.item.value!!
        val quantity = fragment.findViewById<TextView>(R.id.item_quantity)

        if (item.quantity > 1) {
            quantity.text = getString(R.string.item_quantity, item.quantity)
        } else {
            quantity.visibility = View.GONE
        }
    }
}