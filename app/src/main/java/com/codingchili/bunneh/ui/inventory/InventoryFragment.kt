package com.codingchili.bunneh.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingchili.bunneh.R
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.item.ItemFragment
import com.codingchili.bunneh.ui.transform.itemGridAdapter
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.ItemRarity
import com.codingchili.bunneh.ui.dialog.Dialogs
import com.codingchili.bunneh.ui.dialog.InformationDialog
import com.codingchili.bunneh.ui.dialog.NavigableTreeDialog
import com.codingchili.bunneh.ui.dialog.searchFilterTree
import java.util.function.Consumer

class InventoryFragment() : Fragment() {
    private var items = ArrayList<Item>()

    init {
        items.add(
            Item(
                icon = "dagger.png",
                rarity = ItemRarity.mythic,
                quantity = 1,
                title = "Icicle of Doom +5",
                slot = "weapon",
                type = "staff"
            )
        )
        for (i in 0..8) {
            items.addAll(items)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_inventory, container, false)
        val grid = fragment.findViewById<GridView>(R.id.inventory_items)

        fragment.findViewById<View>(R.id.currency_container).setOnClickListener {
            InformationDialog(R.string.liquidity_title, R.string.liquidity_text)
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        /*val swipe = fragment.findViewById<SwipeRefreshLayout>(R.id.pull_refresh)
        swipe.setProgressViewOffset(true, swipe.progressViewStartOffset, swipe.progressViewEndOffset)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }*/

        fragment.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog(
                "Sort",
                searchFilterTree
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        val adapter = itemGridAdapter(
            this,
            inflater,
            Consumer<Item> { item ->
                requireActivity().title = item.title
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.root, ItemFragment().load(item))
                    .addToBackStack(AuctionFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter
        adapter.addAll(items)

        return fragment
    }
}