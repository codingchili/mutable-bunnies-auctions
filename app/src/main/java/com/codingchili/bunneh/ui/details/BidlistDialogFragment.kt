package com.codingchili.bunneh.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.ui.bidListAdapter
import com.codingchili.bunneh.ui.home.AuctionBid
import com.codingchili.bunneh.ui.home.AuctionItem
import com.google.android.material.button.MaterialButton
import java.util.function.Consumer

class BidlistDialogFragment : DialogFragment() {
    private lateinit var auction: AuctionItem

    fun setAuction(auction: AuctionItem): BidlistDialogFragment {
        this.auction = auction
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_bids, container, false)

        view.findViewById<MaterialButton>(R.id.close_button).setOnClickListener {
            dialog?.hide()
        }

        val list = view.findViewById<ListView>(R.id.bid_history)
        val adapter = bidListAdapter(this, inflater, Consumer<AuctionBid> {
            // todo view user profile - sold,bought,funds
        })
        list.adapter = adapter
        adapter.addAll(auction.bids.sortedByDescending { it.value })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog?.setTitle("myFoo")
    }
}