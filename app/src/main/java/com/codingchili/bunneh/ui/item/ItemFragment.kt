package com.codingchili.bunneh.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuctionService
import com.codingchili.bunneh.api.AuthenticationService
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.ui.AppToast
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.dialog.Dialogs
import com.codingchili.bunneh.ui.dialog.NumberInputDialog
import com.codingchili.bunneh.ui.transform.ServerResource
import com.codingchili.bunneh.ui.transform.Type
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.function.Consumer

class ItemFragment : Fragment() {
    private var service = AuctionService.instance
    private var authentication = AuthenticationService.instance
    private lateinit var item: Item

    companion object {
        val TAG = "item.fragment"
    }

    fun load(item: Item): Fragment {
        this.item = item
        return this
    }

    private fun auctionHandler(view: View): Consumer<Int> {
        return Consumer<Int> { initialValue ->
            service.auction(item, initialValue).bindToLifecycle(view).subscribe { response, e ->
                if (e == null) {
                    requireActivity().supportFragmentManager.popBackStack(
                        ItemFragment.TAG,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.root, AuctionFragment().load(response))
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

        requireActivity().title = item.name
        fragment.findViewById<TextView>(R.id.item_description).text = item.description
        fragment.findViewById<TextView>(R.id.item_stats).text = item.stats.toString()
        /*fragment.findViewById<TextView>(R.id.item_owner).text =
            authentication.user()?.name*/

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
        val quantity = fragment.findViewById<TextView>(R.id.item_quantity)
        if (item.quantity > 1) {
            quantity.text = "x" + item.quantity.toString()
        } else {
            quantity.visibility = View.GONE
        }
    }
}