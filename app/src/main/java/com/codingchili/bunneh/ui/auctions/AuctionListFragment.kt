package com.codingchili.bunneh.ui.auctions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.model.Auction

class AuctionListFragment(val auctions: List<Auction>): Fragment() {
    companion object {
        const val TAG = "auction.list"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_auction_list, container, false)

        // populate listview with auctions.
        fragment.findViewById<TextView>(R.id.counter).text = "${auctions.size}"

        return fragment
    }
}