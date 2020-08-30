package com.codingchili.bunneh.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuctionService
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.ui.AppToast
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.dialog.*
import com.codingchili.bunneh.ui.transform.Sorter
import com.codingchili.bunneh.ui.transform.auctionGridAdapter
import java.util.function.Consumer

/**
 * Fragment used to search for auctions.
 */
class SearchFragment : Fragment() {
    private val service = AuctionService.instance
    private val hits by activityViewModels<SearchViewModel>()
    private var sorter = Sorter()

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
                    .add(R.id.root, AuctionFragment().load(it, hits.auctions.value!!))
                    .addToBackStack(AuctionFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter

        hits.auctions.observe(viewLifecycleOwner, Observer {
            fragment.findViewById<ProgressBar>(R.id.progress_search).visibility = View.GONE

            if (it.size > 0) {
                fragment.findViewById<View>(R.id.progress_container).visibility = View.GONE
            } else {
                fragment.findViewById<TextView>(R.id.progress_text).text =
                    "No auctions here, try searching."
            }
            adapter.clear()
            adapter.addAll(sorter.sort(it))
            adapter.notifyDataSetChanged()
        })

        if (hits.auctions.value == null) {
            search("ending soon", fragment)
        }
        return fragment
    }

    private fun bindViewControls(view: View) {
        view.findViewById<View>(R.id.quick_search).setOnClickListener {
            NavigableTreeDialog(
                "Quick Search",
                navigableCategoryTree,
                Consumer<NavigableTree> {
                    search(it.name ?: "", view)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog(
                "Sort",
                searchFilterTree,
                Consumer<NavigableTree> { leaf ->
                    sorter.ascending = leaf.name == getString(R.string.sort_ascending)
                    sorter.setMethodByName(requireContext(), leaf.parent!!.name)
                    hits.auctions.value = sorter.sort(hits.auctions.value!!)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.text_query).setOnClickListener {
            TextSearchDialog(Consumer<String> {
                search(it, view)
            }).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }
    }

    private fun search(query: String, view: View) {
        val container = view.findViewById<View>(R.id.progress_container)
        val text = view.findViewById<TextView>(R.id.progress_text)
        val progress = view.findViewById<ProgressBar>(R.id.progress_search)

        text.text = "Searching for '${query}' .."
        progress.visibility = View.VISIBLE
        container.visibility = View.VISIBLE

        service.search(query).subscribe { auctions, e ->
            if (e == null) {
                hits.auctions.value = auctions
            } else {
                AppToast.show(requireContext(), e.message!!)
                progress.visibility = View.GONE

                if (hits.auctions.value!!.isNotEmpty()) {
                    container.visibility = View.GONE
                } else {
                    // trigger reset.
                    hits.auctions.value = hits.auctions.value
                }
            }
        }
    }
}