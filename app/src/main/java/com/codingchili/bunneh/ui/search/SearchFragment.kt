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
import com.codingchili.bunneh.ui.transform.Sorter
import com.codingchili.bunneh.ui.transform.auctionGridAdapter
import java.util.function.Consumer

/**
 * Fragment used to search for auctions.
 */
class SearchFragment : Fragment() {
    private val service = LocalAuctionService()
    private val hits = MutableLiveData<List<Auction>>(ArrayList())
    private var sorter = Sorter()

    init {
        retainInstance = true
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
                    .add(R.id.root, AuctionFragment().load(it, hits.value!!))
                    .addToBackStack(AuctionFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter
        adapter.addAll(sorter.sort(this.hits.value!!))

        this.hits.observe(viewLifecycleOwner, Observer {
            adapter.clear()
            adapter.addAll(sorter.sort(it))
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
                    sorter.ascending = leaf.name == getString(R.string.sort_ascending)
                    sorter.setMethodByName(requireContext(), leaf.parent!!.name)
                    hits.value = sorter.sort(hits.value!!)
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