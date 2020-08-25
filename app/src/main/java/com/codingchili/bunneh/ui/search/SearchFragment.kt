package com.codingchili.bunneh.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.GridView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.ui.details.DetailFragment
import com.codingchili.bunneh.ui.itemGridAdapter
import java.util.function.Consumer


class SearchFragment : Fragment() {
    private lateinit var searchViewItem: AuctionItem
    private val hits = ArrayList<AuctionItem>() // todo mvp

    init {
        hits.add(
            AuctionItem(
                icon = "flaming_stick.png",
                rarity = ItemRarity.legendary,
                quantity = 1,
                bid = 825000,
                title = "Flaming Stick +4",
                slot = "weapon",
                type = "staff"
            )
        )
        hits.add(
            AuctionItem(
                icon = "branch.png",
                rarity = ItemRarity.common,
                quantity = 1,
                bid = 250,
                title = "Leafy Branch +1",
                slot = "weapon",
                type = "staff"
            )
        )
        hits.add(
            AuctionItem(
                icon = "ring_1.png",
                rarity = ItemRarity.mythic,
                quantity = 1,
                bid = 36000000,
                title = "The Sauring",
                slot = "ring"
            )
        )
        hits.add(
            AuctionItem(
                icon = "apple_green.png",
                rarity = ItemRarity.rare,
                quantity = 99,
                bid = 45,
                title = "Green Apple",
                type = "consumable"
            )
        )
        hits.add(
            AuctionItem(
                icon = "wand_1.png",
                rarity = ItemRarity.rare,
                quantity = 1,
                bid = 11500,
                title = "Spacewand +2",
                slot = "weapon",
                type = "staff"
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

        fragment.findViewById<View>(R.id.quick_search).setOnClickListener {
            NavigableTreeDialog("Quick Search", navigableCategoryTree)
                .show(requireActivity().supportFragmentManager, "foo")
        }

        fragment.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog("Sort", searchFilterTree)
                .show(requireActivity().supportFragmentManager, "foo")
        }

        fragment.findViewById<View>(R.id.text_query).setOnClickListener {
            TextSearchDialog()
                .show(requireActivity().supportFragmentManager, "foo")
        }

        val adapter = itemGridAdapter(this, inflater, Consumer<AuctionItem> {
            requireActivity().title = it.title
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.root, DetailFragment().load(it, hits))
                .addToBackStack("details")
                .commit()
        })
        grid.adapter = adapter
        adapter.addAll(this.hits)
        return fragment
    }
}