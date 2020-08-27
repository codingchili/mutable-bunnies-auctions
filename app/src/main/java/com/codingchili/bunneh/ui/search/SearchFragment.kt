package com.codingchili.bunneh.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.LocalAuctionService
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.dialog.*
import com.codingchili.bunneh.ui.transform.auctionGridAdapter
import java.util.function.Consumer

/**
 * Fragment used to search for auctions.
 */
class SearchFragment : Fragment() {
    private val service = LocalAuctionService()
    private val hits = MutableLiveData<List<Auction>>(ArrayList())
    private var sorter = this::sortByEnd
    private var ascending = false

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
                    .add(R.id.root, AuctionFragment().load(it, hits.value!!))
                    .addToBackStack(AuctionFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter
        adapter.addAll(this.hits.value!!)

        this.hits.observe(viewLifecycleOwner, Observer {
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        })
        return fragment
    }

    private fun bindViewControls(view: View) {
        view.findViewById<View>(R.id.quick_search).setOnClickListener {
            NavigableTreeDialog(
                "Quick Search",
                navigableCategoryTree,
                Consumer<NavigableTree> {
                    service.search(it.query ?: "").subscribe { auction, e ->
                        update(auction, e)
                    }
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog(
                "Sort",
                searchFilterTree,
                Consumer<NavigableTree> { leaf ->
                    ascending = leaf.name == getString(R.string.sort_ascending)

                    when (leaf.parent!!.name) {
                        getString(R.string.sort_bid) -> sorter = this::sortByBid
                        getString(R.string.sort_name) -> sorter = this::sortByName
                        getString(R.string.sort_end) -> sorter = this::sortByEnd
                        getString(R.string.sort_rarity) -> sorter = this::sortByRarity
                    }
                    hits.value = sorter(hits.value!!)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.text_query).setOnClickListener {
            TextSearchDialog(Consumer<String> {
                service.search(it).subscribe { auction, e ->
                    update(auction, e)
                }
            }).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }
    }

    private fun sortByBid(list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.bid } else list.sortedByDescending { it.bid }
    }

    private fun sortByName(list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.item.name } else list.sortedByDescending { it.item.name }
    }

    private fun sortByEnd(list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.end } else list.sortedByDescending { it.end }
    }

    private fun sortByRarity(list: List<Auction>): List<Auction> {
        return if (ascending) list.sortedBy { it.item.rarity } else list.sortedByDescending { it.item.rarity }
    }

    private fun update(auctions: List<Auction>, e: Throwable?) {
        if (e == null) {
            hits.value = auctions
        } else {
            showError(e.message!!)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}