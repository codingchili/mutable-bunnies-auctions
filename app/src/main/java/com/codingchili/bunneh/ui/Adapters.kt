package com.codingchili.bunneh.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.codingchili.bunneh.R
import com.codingchili.bunneh.ui.home.AuctionBid
import com.codingchili.bunneh.ui.home.AuctionItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.function.Consumer

fun bidListAdapter(
    fragment: Fragment,
    inflater: LayoutInflater,
    listener: Consumer<AuctionBid>
): ArrayAdapter<AuctionBid> {
    return object : ArrayAdapter<AuctionBid>(fragment.context!!, R.layout.item_bid) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val bid = getItem(position)!!
            val view = convertView ?: inflater.inflate(
                R.layout.item_bid,
                parent,
                false
            )

            val format = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            view.findViewById<TextView>(R.id.bid_value).text = "${bid.value} Ξ"
            view.findViewById<TextView>(R.id.bid_owner).text = bid.owner
            view.findViewById<TextView>(R.id.bid_date).text = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(bid.date), ZoneId.systemDefault())
                .format(format)
                .replace("T", " ")

            return view
        }
    }
}

fun itemGridAdapter(
    fragment: Fragment,
    inflater: LayoutInflater,
    listener: Consumer<AuctionItem>
): ArrayAdapter<AuctionItem> {
    return object : ArrayAdapter<AuctionItem>(fragment.context!!, R.layout.item_thumbnail) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val item = getItem(position)!!
            val view = convertView ?: inflater.inflate(
                R.layout.item_thumbnail,
                parent,
                false
            )
            Glide.with(context)
                .load(fragment.getString(R.string.resources_host) + "/resources/gui/item/icon/${item.icon}")
                .into(view.findViewById(R.id.item_image))

            view.setOnClickListener { listener.accept(item) }

            view.findViewById<View>(R.id.item_rarity)
                .setBackgroundColor(fragment.resources.getColor(item.rarity.resource, null))

            view.findViewById<TextView>(R.id.item_bid).text = formatValue(item.bid)
            view.findViewById<TextView>(R.id.item_title).text = item.title

            val quantity = view.findViewById<TextView>(R.id.item_quantity)
            if (item.quantity > 1) {
                quantity.text = "x${item.quantity}"
            } else {
                quantity.visibility = View.GONE
            }
            return view
        }
    }
}