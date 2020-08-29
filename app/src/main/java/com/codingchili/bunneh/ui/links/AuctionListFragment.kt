package com.codingchili.bunneh.ui.links

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuthenticationService
import com.codingchili.bunneh.api.LocalAuthenticationService
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.AuctionState
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.transform.renderItemThumbnail
import com.codingchili.bunneh.ui.transform.setupChronometerFromAuction

class AuctionListFragment(private val auctions: List<Auction>) : Fragment() {
    companion object {
        const val TAG = "auction.list"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_auction_list, container, false)
        val list = fragment.findViewById<ListView>(R.id.auction_list)
        val adapter = object : ArrayAdapter<Auction>(requireContext(), R.layout.item_auction) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val auction = getItem(position)!!
                val entry = convertView ?: inflater.inflate(R.layout.item_auction, parent, false)

                entry.findViewById<TextView>(R.id.item_seller).text = "Sold by ${auction.seller!!.name}"
                renderItemThumbnail(this@AuctionListFragment, entry, auction = auction)

                val bid = auction.bids.firstOrNull()
                if (bid != null) {
                    entry.findViewById<TextView>(R.id.item_bid_owner).text = "by ${bid.owner.name}"
                } else {
                    entry.findViewById<TextView>(R.id.item_bid_owner).text = ""
                }

                val state = AuctionState.fromAuction(
                    auction,
                    AuthenticationService.instance.current()!!.user
                )
                entry.findViewById<ImageView>(R.id.status_icon).setImageResource(state.icon)

                val chronometer = entry.findViewById<Chronometer>(R.id.auction_end)
                setupChronometerFromAuction(chronometer, auction)

                entry.setOnClickListener {
                    requireActivity().title = auction.item.name
                    requireActivity().supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(R.id.root, AuctionFragment().load(auction))
                        .addToBackStack(AuctionFragment.TAG)
                        .commit()
                }
                return entry
            }
        }
        list.adapter = adapter
        adapter.addAll(auctions.sortedBy { it.end })
        return fragment
    }
}