package com.codingchili.bunneh.ui.auction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.codingchili.bunneh.R
import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.Bid
import com.codingchili.bunneh.ui.transform.bidListAdapter
import com.google.android.material.button.MaterialButton
import java.util.function.Consumer

class BidlistDialogFragment : DialogFragment() {
    private lateinit var auction: Auction

    fun setAuction(auction: Auction): BidlistDialogFragment {
        this.auction = auction
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_bids, container, false)

        view.findViewById<MaterialButton>(R.id.close_button).setOnClickListener { dismiss() }

        val list = view.findViewById<ListView>(R.id.bid_history)
        val adapter = bidListAdapter(
            this,
            inflater,
            Consumer<Bid> {
                // todo view user profile - sold,bought,funds
            })
        list.adapter = adapter
        adapter.addAll(auction.bids)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}