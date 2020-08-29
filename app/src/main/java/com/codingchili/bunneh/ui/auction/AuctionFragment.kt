package com.codingchili.bunneh.ui.auction

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuctionService
import com.codingchili.bunneh.api.AuthenticationService
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.AuctionState
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.ui.dialog.Dialogs
import com.codingchili.bunneh.ui.dialog.NumberInputDialog
import com.codingchili.bunneh.ui.transform.RecyclerAdapter
import com.codingchili.bunneh.ui.transform.ServerResource
import com.codingchili.bunneh.ui.transform.formatValue
import com.codingchili.bunneh.ui.transform.setupChronometerFromAuction
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.function.Consumer


/*1. quick action buttons on search, categories/search dialog/recent LinLay*/
/*2. inventory workspace, current balance - usable balance, tap to create sell auction*/
/*3. active orders: selling buying / tab switcher*/
/*4. notifications workspace: show when someone bids higher/auction ends etc. - event log on server per account.*/
/*5. connect data to layout, related items etc.*/
/*6. connect data to server - bidirectional flow*/


class AuctionFragment : Fragment() {
    private var authentication = AuthenticationService.instance
    private var auctions = AuctionService.instance
    private lateinit var auction: Auction
    private var hits: List<Auction> = ArrayList()

    companion object {
        const val TAG = "details"
    }

    fun load(auction: Auction): AuctionFragment {
        this.auction = auction
        return this
    }

    fun load(auction: Auction, hits: List<Auction>): AuctionFragment {
        this.auction = auction
        this.hits = hits
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_auction, container, false)
        val swipe = fragment.findViewById<SwipeRefreshLayout>(R.id.pull_refresh)
        swipe.setOnRefreshListener { swipe.isRefreshing = false }

        update(auction, fragment)

        fragment.findViewById<FloatingActionButton>(R.id.back)
            .setOnClickListener { requireActivity().onBackPressed() }
        return fragment
    }

    private fun update(auction: Auction, fragment: View) {
        this.auction = auction
        val item = auction.item
        requireActivity().title = item.name

        fragment.findViewById<TextView>(R.id.item_description).text = item.description
        fragment.findViewById<TextView>(R.id.item_stats).text = item.stats.toString()
        fragment.findViewById<TextView>(R.id.item_bid).text = formatValue(auction.high())
        fragment.findViewById<TextView>(R.id.item_seller).text = auction.seller!!.name
        fragment.findViewById<TextView>(R.id.auction_bid_count).text = auction.bids.size.toString()

        fragment.findViewById<RelativeLayout>(R.id.bid_list).setOnClickListener {
            BidlistDialogFragment()
                .setAuction(auction)
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        val quantity = fragment.findViewById<TextView>(R.id.item_quantity)
        if (item.quantity > 1) {
            quantity.text = "x" + item.quantity.toString()
        } else {
            quantity.visibility = View.GONE
        }

        fragment.findViewById<ImageView>(R.id.auction_favorite).setOnClickListener {
            it.background = ResourcesCompat.getDrawable(resources, R.drawable.icon_star, null)
        }

        val chronometer = fragment.findViewById<Chronometer>(R.id.auction_end)
        setupChronometerFromAuction(chronometer, auction, listener = Runnable {
            updateAuctionState(fragment)
        })

        ServerResource.icon(fragment.findViewById(R.id.item_image), item.icon)
        updateLabels(fragment, item)
        updateRelatedHits(fragment)
        updateAuctionState(fragment)
    }

    private fun updateLabels(fragment: View, item: Item) {
        setLabel(fragment.findViewById(R.id.item_slot), item.slot, getColorForItemType())
        setLabel(fragment.findViewById(R.id.item_type), item.type, R.color.type_default)
        setLabel(
            fragment.findViewById(R.id.item_rarity),
            item.rarity.toString(),
            item.rarity.resource
        )
    }

    private fun updateRelatedHits(fragment: View) {
        val relatedContainer = fragment.findViewById<View>(R.id.related_items)
        val relatedListView = fragment.findViewById(R.id.horizontal_scroll) as RecyclerView

        if (hits.isEmpty()) {
            relatedContainer.layoutParams.height = 0
            relatedContainer.visibility = View.INVISIBLE
        } else {
            val related = hits.subList(0, Math.min(hits.size - 1, 16)).filter { it != auction }
            relatedListView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            relatedListView.adapter =
                RecyclerAdapter(
                    this,
                    related,
                    Consumer<Any> {
                        it as Auction
                        requireActivity().supportFragmentManager.popBackStack(
                            TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        );
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.root, AuctionFragment().load(it, hits))
                            .addToBackStack(TAG)
                            .commit()
                    })
        }
    }

    private fun updateAuctionState(fragment: View) {
        val state = AuctionState.fromAuction(auction, authentication.current()!!.user)
        val button = fragment.findViewById<MaterialButton>(R.id.button)
        val status = fragment.findViewById<MaterialButton>(R.id.status)

        Log.e("foo", "state = ${state.name}")

        if (state.interative()) {
            status.visibility = View.GONE
            button.visibility = View.VISIBLE
            button.text = getString(state.string)
            button.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(state.color))

            button.setOnClickListener {
                NumberInputDialog()
                    .onResponse(Consumer<Int> {
                        auctions.bid(it, auction).subscribe { response, error ->
                            if (error == null) {
                                update(response, fragment)
                            } else {
                                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }).show(requireActivity().supportFragmentManager, Dialogs.TAG)
            }
        } else {
            button.visibility = View.GONE
            status.visibility = View.VISIBLE
            status.text = getString(state.string)
            status.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(state.color))
        }
    }

    private fun setLabel(view: TextView, value: String?, color: Int) {
        if (value != null) {
            val background = view.background.mutate() as GradientDrawable
            background.setColor(ContextCompat.getColor(requireContext(), color));
            view.text = value
            view.background = background
        } else {
            view.visibility = View.GONE
        }
    }

    private fun getColorForItemType() = when (auction.item.slot) {
        "consumable" -> R.color.type_consumable
        "weapon" -> R.color.type_weapon
        "armor" -> R.color.type_armor
        "quest" -> R.color.type_quest
        else -> R.color.type_default
    }
}