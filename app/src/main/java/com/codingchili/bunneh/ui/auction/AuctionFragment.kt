package com.codingchili.bunneh.ui.auction

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuctionService
import com.codingchili.bunneh.api.AuthenticationService
import com.codingchili.bunneh.model.AuctionState
import com.codingchili.bunneh.ui.AppToast
import com.codingchili.bunneh.ui.dialog.Dialogs
import com.codingchili.bunneh.ui.dialog.NumberInputDialog
import com.codingchili.bunneh.ui.links.AuctionListFragment
import com.codingchili.bunneh.ui.transform.*
import com.codingchili.banking.model.Auction
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.function.Consumer


class AuctionFragment : Fragment() {
    private var authentication = AuthenticationService.instance
    private var service = AuctionService.instance
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

    private fun handleSellerClick(fragment: View): View.OnClickListener {
        return View.OnClickListener {
            service.search("seller=${auction.seller}}").bindToLifecycle(fragment)
                .subscribe { response, e ->
                    if (e == null) {
                        if (response.isEmpty()) {
                            AppToast.show(context, "No auctions found.")
                        } else {
                            requireActivity().title = "Auctions by ${auction.seller}"
                            requireActivity().supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )
                                .add(R.id.root, AuctionListFragment(response))
                                .addToBackStack(AuctionListFragment.TAG)
                                .commit()
                        }
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

        val seller = fragment.findViewById<TextView>(R.id.item_seller)
        seller.text = auction.seller

        // list sellers other auctions if available.
        seller.setOnClickListener(handleSellerClick(fragment))

        fragment.findViewById<TextView>(R.id.item_description).text = item.description
        fragment.findViewById<TextView>(R.id.item_stats).text = item.stats.toString()
        fragment.findViewById<TextView>(R.id.item_bid).text = formatValue(auction.high())
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

        var favorited = false

        // hide until favorite status is loaded.
        val favorite = fragment.findViewById<ImageView>(R.id.auction_favorite)
        favorite.visibility = View.GONE

        service.favorites().bindToLifecycle(fragment).subscribe { favorites, e ->
            if (e == null) {
                fragment.findViewById<ImageView>(R.id.auction_favorite).visibility = View.VISIBLE
                favorited = (favorites.contains(auction))
                updateFavorite(favorite, favorited)
            }
        }

        fragment.findViewById<ImageView>(R.id.auction_favorite).setOnClickListener {
            // immediate feedback to improve responsiveness.
            favorited = !favorited
            updateFavorite(it as ImageView, favorited)

            service.favorite(auction, favorited).bindToLifecycle(fragment)
                .subscribe { response, e ->
                    if (e != null) {
                        // reverse on error
                        favorited = !favorited
                        updateFavorite(it, favorited)
                        AppToast.show(context, e.message)
                    }
                }
        }

        val chronometer = fragment.findViewById<Chronometer>(R.id.auction_end)
        setupChronometerFromAuction(chronometer, auction, listener = Runnable {
            updateAuctionState(fragment)
        })

        ServerResource.icon(fragment.findViewById(R.id.item_image), item.icon)
        Type.updateLabels(fragment, item)
        updateRelatedHits(fragment)
        updateAuctionState(fragment)
    }

    private fun updateFavorite(view: ImageView, favorite: Boolean) {
        if (favorite) {
            view.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.icon_star,
                    null
                )
            )
        } else {
            view.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.icon_star_outline,
                    null
                )
            )
        }
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

    private fun onBidHandler(fragment: View): Consumer<Int> {
        return Consumer<Int> {
            service.bid(it, auction).bindToLifecycle(fragment).subscribe { response, e ->
                if (e == null) {
                    update(response, fragment)
                } else {
                    AppToast.show(context, e.message)
                }
            }
        }
    }

    private fun updateAuctionState(fragment: View) {
        val user = authentication.user()!!
        val state = AuctionState.fromAuction(auction, user)
        val button = fragment.findViewById<MaterialButton>(R.id.button)
        val status = fragment.findViewById<MaterialButton>(R.id.status)

        if (state.interative() && user != auction.seller) {
            status.visibility = View.INVISIBLE
            button.visibility = View.VISIBLE

            // check if user already has bid once.
            if (auction.bids.find { it.owner == user } == null) {
                button.text = getString(R.string.auction_bid)
            } else {
                button.text = getString(R.string.auction_overbid)
            }
            button.backgroundTintList =
                ColorStateList.valueOf(requireContext().getColor(state.color))

            button.setOnClickListener {
                NumberInputDialog(onBidHandler(fragment))
                    .show(requireActivity().supportFragmentManager, Dialogs.TAG)
            }
        } else {
            button.visibility = View.GONE
            status.visibility = View.VISIBLE
            status.text = getString(state.string)
            status.backgroundTintList =
                ColorStateList.valueOf(requireContext().getColor(state.color))
        }
    }
}