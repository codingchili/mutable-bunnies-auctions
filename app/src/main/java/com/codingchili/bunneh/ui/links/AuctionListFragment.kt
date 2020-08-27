package com.codingchili.bunneh.ui.links

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.ItemRarity
import com.codingchili.bunneh.ui.transform.renderItemThumbnail
import java.util.function.Consumer

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
                val entry = convertView ?: inflater.inflate(R.layout.item_auction, parent, false)
                val card = entry.findViewById<RelativeLayout>(R.id.card)
                val margins = (card.layoutParams as ViewGroup.MarginLayoutParams)
                margins.setMargins(0, 0, 0, 0)
                margins.marginStart = 0
                card.elevation = 4.0f

                renderItemThumbnail(this@AuctionListFragment, Consumer<Any> {
                    // on click show auction fragment
                }, entry, auction = getItem(position))
                return entry
            }
        }
        list.adapter = adapter
        adapter.addAll(auctions.sortedByDescending { it.end })
        return fragment
    }
}