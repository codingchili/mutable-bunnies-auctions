package com.codingchili.bunnies.ui.auction

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.QueryType
import com.codingchili.bunnies.R
import com.codingchili.bunnies.api.AuctionService
import com.codingchili.bunnies.api.AuthenticationService
import com.codingchili.bunnies.model.AuctionState
import com.codingchili.bunnies.ui.AppToast
import com.codingchili.bunnies.ui.dialog.Dialogs
import com.codingchili.bunnies.ui.dialog.NumberInputDialog
import com.codingchili.bunnies.ui.links.AuctionListFragment
import com.codingchili.bunnies.ui.links.AuctionListViewModel
import com.codingchili.bunnies.ui.transform.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.function.Consumer

/**
 * The auction fragment is responsible for displaying information
 * about an auction, either active or past.
 */
class AuctionFragment : Fragment() {
    private var authentication = AuthenticationService.instance
    private var service = AuctionService.instance
    private val model by activityViewModels<AuctionViewModel>()
    private val auctions by activityViewModels<AuctionListViewModel>()

    companion object {
        const val TAG = "details"
    }



    private fun handleSellerClick(fragment: View): View.OnClickListener {
        val auction = this.model.auction.value!!
        return View.OnClickListener {
            service.search(QueryType.seller, auction.seller).bindToLifecycle(fragment)
                .subscribe { response, e ->
                    if (e == null) {
                        if (response.isEmpty()) {
                            AppToast.show(context, getString(R.string.no_auctions_found))
                        } else {
                            this.auctions.list.value = response

                            requireActivity().title =
                                getString(R.string.title_auctions_by, auction.seller)

                            requireActivity().supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )
                                .add(R.id.root, AuctionListFragment())
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
        swipe.setOnRefreshListener {
            service.findById(this.model.auction.value!!.id!!).bindToLifecycle(fragment).subscribe { response, e ->
                if (e == null) {
                    update(response, fragment)
                }
                swipe.isRefreshing = false
            }
        }

        update(this.model.auction.value!!, fragment)

        fragment.findViewById<FloatingActionButton>(R.id.back)
            .setOnClickListener { requireActivity().onBackPressed() }
        return fragment
    }

    // update source list with updated auction.
    private fun propagate(auction: Auction) {
        if (this.model.list.value != null) {
            this.model.list.postValue(this.model.list.value!!.map { if (it.id == auction.id) auction else it })
        }
    }

    private fun update(auction: Auction, fragment: View) {
        propagate(auction)

        this.model.auction.value = auction
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
            quantity.text = getString(R.string.item_quantity, item.quantity)
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
                favorited = favorites.any { it.id == auction.id }
                updateFavorite(favorite, favorited)
            }
        }

        fragment.findViewById<ImageView>(R.id.auction_favorite).setOnClickListener {
            // immediate feedback to improve responsiveness.
            favorited = !favorited
            updateFavorite(it as ImageView, favorited)

            service.favorite(auction, favorited).bindToLifecycle(fragment)
                .subscribe { _, e ->
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
        val hits = this.model.list
        val auction = this.model.auction.value!!

        if (hits.value.isNullOrEmpty()) {
            relatedContainer.layoutParams.height = 0
            relatedContainer.visibility = View.INVISIBLE
        } else {
            val hits = hits.value!!
            val related = hits.subList(0, Math.min(hits.size - 1, 16)).filter { it.id != auction.id }
            relatedListView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            relatedListView.adapter =
                RecyclerAdapter(
                    this,
                    related,
                    Consumer<Any> {
                        this.model.auction.value = it as Auction

                        requireActivity().supportFragmentManager.popBackStack(
                            TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        );
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.root, AuctionFragment())
                            .addToBackStack(TAG)
                            .commit()
                    })
        }
    }

    private fun onBidHandler(fragment: View): Consumer<Int> {
        return Consumer<Int> {
            service.bid(it, this.model.auction.value!!).bindToLifecycle(fragment).subscribe { response, e ->
                if (e == null) {
                    update(response, fragment)
                } else {
                    AppToast.show(context, e.message)
                }
            }
        }
    }

    private fun updateAuctionState(fragment: View) {
        val auction = this.model.auction.value!!
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