package com.codingchili.bunneh.ui.details

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.codingchili.bunneh.R
import com.codingchili.bunneh.ui.formatValue
import com.codingchili.bunneh.ui.home.AuctionItem
import com.codingchili.bunneh.ui.itemGridAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream


/*1. quick action buttons on search, categories/search dialog/recent LinLay*/
/*2. inventory workspace, current balance - usable balance, tap to create sell auction*/
/*3. active orders: selling buying / tab switcher*/
/*4. notifications workspace: show when someone bids higher/auction ends etc. - event log on server per account.*/
/*5. connect data to layout, related items etc.*/
/*6. connect data to server - bidirectional flow*/
class DetailFragment : Fragment() {
    private lateinit var item: AuctionItem
    private lateinit var hits: List<AuctionItem>

    fun load(item: AuctionItem, hits: List<AuctionItem>): Fragment {
        this.item = item
        this.hits = hits
        return this
    }

    private val chronoCountdownHandler = Chronometer.OnChronometerTickListener { chronometer ->
        val now = ZonedDateTime.now()
        var then =
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(chronometer!!.base),
                ZoneId.systemDefault()
            )

        chronometer.text =
            Stream.of(ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS)
                .map {
                    val diff = it.between(now, then)
                    then = it.addTo(then, -diff)
                    Pair<Long, ChronoUnit>(diff, it)
                }
                .filter { it.first > 0 }
                .map {
                    " ${it.first} ${it.second.name.toLowerCase(Locale.getDefault())}"
                }
                .toArray()
                .joinToString(",")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_details, container, false)

        activity!!.title = item.title

        fragment.findViewById<TextView>(R.id.item_description).text = item.description
        fragment.findViewById<TextView>(R.id.item_stats).text = item.stats
        fragment.findViewById<TextView>(R.id.item_bid).text = formatValue(item.bid)
        fragment.findViewById<TextView>(R.id.item_seller).text = item.seller
        fragment.findViewById<TextView>(R.id.auction_bid_count).text = item.bids.size.toString()

        fragment.findViewById<RelativeLayout>(R.id.bid_list).setOnClickListener {
            BidlistDialogFragment()
                .setAuction(item)
                .show(activity!!.supportFragmentManager, "foo")
        }

        val swipe = fragment.findViewById<SwipeRefreshLayout>(R.id.pull_refresh)
        swipe.setOnRefreshListener {
            // pull item/auction data from server.
            activity!!.title = "UPDATED FROM PULL-DN"
            swipe.isRefreshing = false
        }

        val quantity = fragment.findViewById<TextView>(R.id.item_quantity)
        if (item.quantity > 1) {
            quantity.text = "x" + item.quantity.toString()
        } else {
            quantity.visibility = View.GONE
        }

        val chronometer = fragment.findViewById<Chronometer>(R.id.auction_end)
        chronometer.base = item.end
        chronometer.isCountDown = true
        chronometer.onChronometerTickListener = chronoCountdownHandler
        chronometer.start()

        setLabel(fragment.findViewById(R.id.item_slot), item.slot, getColorForItemType(item))
        setLabel(fragment.findViewById(R.id.item_type), item.type, R.color.type_default)
        setLabel(
            fragment.findViewById(R.id.item_rarity),
            item.rarity.toString(),
            item.rarity.resource
        )

        Glide.with(context!!)
            .load(getString(R.string.resources_host) + "/resources/gui/item/icon/${item.icon}")
            .into(fragment.findViewById(R.id.item_image))

        val grid = fragment.findViewById<GridView>(R.id.search_related)
        val adapter = itemGridAdapter(this, inflater, Consumer<AuctionItem> {
            activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.root, DetailFragment().load(it, hits))
                .remove(this)
                .commit()
        })
        grid.adapter = adapter

        val start = hits.indexOf(item)
        var related = hits.subList(Math.abs(start - 8), start + Math.min(16, hits.size - 1))
        related = related.subList(0, Math.min(16, related.size - 1))
        adapter.addAll(related)

        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            (related.size * (96 + 8 + 1)).toFloat(),
            resources.displayMetrics
        ).toInt()

        fragment.findViewById<LinearLayout>(R.id.horizontal_scroll).layoutParams.width = width
        fragment.findViewById<FloatingActionButton>(R.id.back).setOnClickListener {
            activity!!.onBackPressed()
        }
        return fragment
    }

    private fun setLabel(view: TextView, value: String?, color: Int) {
        if (value != null) {
            val background = view.background.mutate() as GradientDrawable
            background.setColor(ContextCompat.getColor(context!!, color));
            view.text = value
            view.background = background
        } else {
            view.visibility = View.GONE
        }
    }

    private fun getColorForItemType(item: AuctionItem) = when (item.slot) {
        "consumable" -> R.color.type_consumable
        "weapon" -> R.color.type_weapon
        "armor" -> R.color.type_armor
        "quest" -> R.color.type_quest
        else -> R.color.type_default
    }
}