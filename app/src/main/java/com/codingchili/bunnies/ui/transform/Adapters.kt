package com.codingchili.bunnies.ui.transform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.Bid
import com.codingchili.banking.model.Item
import com.codingchili.bunnies.R
import com.codingchili.bunnies.model.Rarity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.function.Consumer

private val DATE_FORMAT = DateTimeFormatter.ofPattern("E d, HH:mm:ss")

/**
 * Bunch of adapters placed here for reuse.
 *
 * Most adapters follow the pattern of accepting a fragment, inflater and listener as inputs.
 * The listener is invoked when the item is clicked.
 */


/**
 * Adapter used to create the bid list in the auction view.
 */
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

            return view
        }
    }
}

/**
 * Adapter used for the thumbnails in the 'related items' container of the auction fragment.
 */
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

/**
 * Adapter used to create the grid used for search hits.
 */
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

/**
 * Adapter used to create the grid used for the users inventory.
 */
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

/**
 * Reusable adapter implementation, used for rendering a thumbnail. Used in the auction fragment
 * for related items, in the auction lists and in the users inventory.
 */
fun renderItemThumbnail(
    fragment: Fragment,
    view: View,
    listener: Consumer<Any>? = null,
    auction: Auction? = null,
    item: Item? = null
): View {
    val item = (auction?.item ?: item)!!

    ServerResource.icon(view.findViewById(R.id.item_image), item.icon)

    if (listener != null) {
        view.setOnClickListener { listener.accept((auction ?: item)) }
    } else {
        view.isClickable = false
    }

    view.findViewById<TextView>(R.id.item_title).text = item.name
    view.findViewById<View>(R.id.item_rarity)
        .setBackgroundColor(fragment.resources.getColor(
            Rarity.resource(item), null))

    if (auction != null) {
        view.findViewById<TextView>(R.id.item_bid).text =
            formatValue(auction.high())
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