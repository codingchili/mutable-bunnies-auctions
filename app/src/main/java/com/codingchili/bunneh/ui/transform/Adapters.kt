package com.codingchili.bunneh.ui.transform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codingchili.bunneh.R
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Bid
import com.codingchili.bunneh.model.Item
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.function.Consumer

private val DATE_FORMAT = DateTimeFormatter.ofPattern("E d, HH:mm:ss")

fun bidListAdapter(
    fragment: Fragment,
    inflater: LayoutInflater,
    listener: Consumer<Bid>
): ArrayAdapter<Bid> {
    return object : ArrayAdapter<Bid>(fragment.requireContext(), R.layout.item_bid) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val bid = getItem(position)!!
            val view = convertView ?: inflater.inflate(
                R.layout.item_bid,
                parent,
                false
            )

            view.findViewById<TextView>(R.id.item_bid_value).text = formatValue(bid.value)
            view.findViewById<TextView>(R.id.item_bid_owner).text = bid.owner
            view.findViewById<TextView>(R.id.bid_date).text = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(bid.date), ZoneId.systemDefault())
                .format(DATE_FORMAT)
                .replace("T", " ")

            return view
        }
    }
}

class RecyclerAdapter(
    val fragment: Fragment,
    val list: List<Auction>,
    val listener: Consumer<Any>
) :
    RecyclerView.Adapter<RecyclerAdapter.Thumbnail>() {

    inner class Thumbnail(view: View) : RecyclerView.ViewHolder(view) {
        var view = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Thumbnail {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_thumbnail,
                parent,
                false
            )
        return Thumbnail(itemView)
    }

    override fun onBindViewHolder(holder: Thumbnail, position: Int) {
        val view = holder.view
        val item = list[position]

        // inefficient use of RecyclerAdapter, allows sharing code with Grid adapter.
        renderItemThumbnail(
            fragment,
            view,
            listener = listener,
            auction = item
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

fun auctionGridAdapter(
    fragment: Fragment,
    inflater: LayoutInflater,
    listener: Consumer<Auction>
): ArrayAdapter<Auction> {
    return object : ArrayAdapter<Auction>(fragment.requireContext(), R.layout.item_thumbnail) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val auction = getItem(position)!!
            val view = convertView ?: inflater.inflate(R.layout.item_thumbnail, parent, false)
            return renderItemThumbnail(
                fragment,
                view,
                listener = Consumer<Any> {
                    listener.accept(it as Auction)
                },
                auction = auction
            )
        }
    }
}

fun itemGridAdapter(
    fragment: Fragment,
    inflater: LayoutInflater,
    listener: Consumer<Item>
): ArrayAdapter<Item> {
    return object : ArrayAdapter<Item>(fragment.requireContext(), R.layout.item_thumbnail) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val item = getItem(position)!!
            val view = convertView ?: inflater.inflate(R.layout.item_thumbnail, parent, false)
            return renderItemThumbnail(
                fragment,
                view,
                listener = Consumer<Any> {
                    listener.accept(it as Item)
                },
                item = item
            )
        }
    }
}

fun renderItemThumbnail(
    fragment: Fragment,
    view: View,
    listener: Consumer<Any>? = null,
    auction: Auction? = null,
    item: Item? = null
): View {
    val item = (auction?.item ?: item)!!

    Glide.with(fragment.requireActivity())
        .load(fragment.getString(R.string.resources_host) + "/resources/gui/item/icon/${item.icon}")
        .into(view.findViewById(R.id.item_image))

    if (listener != null) {
        view.setOnClickListener { listener.accept((auction ?: item)) }
    } else {
        view.isClickable = false
    }

    view.findViewById<TextView>(R.id.item_title).text = item.name
    view.findViewById<View>(R.id.item_rarity)
        .setBackgroundColor(fragment.resources.getColor(item.rarity.resource, null))

    if (auction != null) {
        view.findViewById<TextView>(R.id.item_bid).text =
            formatValue(auction.bid)
    } else {
        view.findViewById<TextView>(R.id.item_bid).visibility = View.GONE
    }

    val quantity = view.findViewById<TextView>(R.id.item_quantity)
    if (item.quantity > 1) {
        quantity.text = "x${item.quantity}"
    } else {
        quantity.visibility = View.GONE
    }
    return view
}