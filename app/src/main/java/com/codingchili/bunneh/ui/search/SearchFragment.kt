package com.codingchili.bunneh.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.ItemRarity
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.dialog.*
import com.codingchili.bunneh.ui.transform.auctionGridAdapter
import java.util.function.Consumer


class SearchFragment : Fragment() {
    private val hits = ArrayList<Auction>()

    init {
        hits.add(
            Auction(
                bid = 825000,
                item = Item(
                    icon = "flaming_stick.png",
                    rarity = ItemRarity.legendary,
                    quantity = 1,
                    name = "Flaming Stick +4",
                    slot = "weapon",
                    type = "staff"
                )
            )
        )
        hits.add(
            Auction(
                bid = 250,
                item = Item(
                    icon = "branch.png",
                    rarity = ItemRarity.common,
                    quantity = 1,
                    name = "Leafy Branch +1",
                    slot = "weapon",
                    type = "staff"
                )
            )
        )
        hits.add(
            Auction(
                bid = 36000000,
                item = Item(
                    icon = "ring_1.png",
                    rarity = ItemRarity.mythic,
                    quantity = 1,
                    name = "The Sauring",
                    slot = "ring"
                )
            )
        )
        hits.add(
            Auction(
                bid = 45,
                item = Item(
                    icon = "apple_green.png",
                    rarity = ItemRarity.rare,
                    quantity = 99,
                    name = "Green Apple",
                    type = "consumable"
                )
            )
        )
        hits.add(
            Auction(
                bid = 11500,
                item = Item(
                    icon = "wand_1.png",
                    rarity = ItemRarity.rare,
                    quantity = 1,
                    name = "Spacewand +2",
                    slot = "weapon",
                    type = "staff"
                )
            )
        )
        hits.addAll(hits)
        hits.addAll(hits)
        hits.addAll(hits)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_search, container, false)
        val grid = fragment.findViewById<GridView>(R.id.search_hits)

        bindViewControls(fragment)

        val adapter = auctionGridAdapter(
            this,
            inflater,
            Consumer<Auction> {
                requireActivity().title = it.item.name
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.root, AuctionFragment().load(it, hits))
                    .addToBackStack(AuctionFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter
        adapter.addAll(this.hits)
        return fragment
    }

    private fun bindViewControls(view: View) {
        view.findViewById<View>(R.id.quick_search).setOnClickListener {
            NavigableTreeDialog(
                "Quick Search",
                navigableCategoryTree
            )
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog(
                "Sort",
                searchFilterTree
            )
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.text_query).setOnClickListener {
            TextSearchDialog()
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }
    }
}