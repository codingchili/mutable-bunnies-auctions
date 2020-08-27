package com.codingchili.bunneh.ui.links

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.LocalAuctionService
import com.codingchili.bunneh.model.Auction

class AuctionListFragment(private val auctions: List<Auction>) : Fragment() {
    companion object {
        const val TAG = "auction.list"
    }

    init {
        Log.e("foo", "start search on thread ${Thread.currentThread().id}")
        LocalAuctionService().search("foo").subscribe {
            Log.e("foo", "handle search result ${it.first().item.name} thread ${Thread.currentThread().id}")
        }
        Log.e("foo", "business as usual ${Thread.currentThread().id}")
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