package com.codingchili.bunnies.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.codingchili.banking.model.Auction
import com.codingchili.banking.model.QueryType
import com.codingchili.bunnies.R
import com.codingchili.bunnies.api.AuctionService
import com.codingchili.bunnies.ui.AppToast
import com.codingchili.bunnies.ui.auction.AuctionFragment
import com.codingchili.bunnies.ui.auction.AuctionViewModel
import com.codingchili.bunnies.ui.dialog.*
import com.codingchili.bunnies.ui.transform.Sorter
import com.codingchili.bunnies.ui.transform.auctionGridAdapter
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.function.Consumer

/**
 * Fragment used to search for auctions, using free text and categories with
 * support for ordering the results.
 */
class SearchFragment : Fragment() {
    private val service = AuctionService.instance
    private val hits by activityViewModels<SearchViewModel>()
    private val shared by activityViewModels<AuctionViewModel>()
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
                this.shared.auction.value = it
                this.shared.list = hits.auctions

                requireActivity().title = it.item.name
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.root, AuctionFragment())
                    .addToBackStack(AuctionFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter

        hits.auctions.observe(viewLifecycleOwner, Observer {
            fragment.findViewById<ProgressBar>(R.id.progress_search).visibility = View.GONE

            adapter.clear()
            if (it.isNullOrEmpty()) {
                fragment.findViewById<TextView>(R.id.progress_text).text =
                    getString(R.string.no_auctions_here)
            } else {
                fragment.findViewById<View>(R.id.progress_container).visibility = View.GONE
                adapter.addAll(sorter.sort(it))
            }
            adapter.notifyDataSetChanged()
        })

        if (hits.auctions.value == null) {
            search(QueryType.ending_soon, fragment)
        }
        return fragment
    }

    private fun bindViewControls(view: View) {
        view.findViewById<View>(R.id.quick_search).setOnClickListener {
            NavigableTreeDialog(
                R.string.dialog_quick_search,
                navigableCategoryTree,
                Consumer<NavigableTree> {
                    search(it.query(), view, it.param)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog(
                R.string.dialog_sort,
                sortAuctionsTree,
                Consumer<NavigableTree> { leaf ->
                    sorter.ascending = leaf.name == R.string.nav_ascending
                    sorter.setMethodByName(leaf.parent!!.name)
                    hits.auctions.value = sorter.sort(hits.auctions.value!!)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        view.findViewById<View>(R.id.text_query).setOnClickListener {
            TextSearchDialog(Consumer<String> {
                search(QueryType.text, view, it)
            }).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }
    }

    private fun search(query: QueryType, view: View, param: String? = null) {
        val container = view.findViewById<View>(R.id.progress_container)
        val text = view.findViewById<TextView>(R.id.progress_text)
        val progress = view.findViewById<ProgressBar>(R.id.progress_search)

        text.text = getString(R.string.searching_text, query.readableName())
        progress.visibility = View.VISIBLE
        container.visibility = View.VISIBLE

        service.search(query, param).bindToLifecycle(view).subscribe { auctions, e ->
            if (e == null) {
                hits.auctions.value = auctions
            } else {
                AppToast.show(context, e.message)
                progress.visibility = View.GONE

                if (hits.auctions.value.isNullOrEmpty()) {
                    // trigger reset.
                    hits.auctions.value = hits.auctions.value
                } else {
                    container.visibility = View.GONE
                }
            }
        }
    }
}