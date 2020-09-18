package com.codingchili.bunnies.ui.links

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.codingchili.bunnies.R
import com.codingchili.bunnies.api.AuctionService
import com.codingchili.bunnies.ui.AppToast
import com.codingchili.bunnies.ui.dialog.Divider
import com.codingchili.bunnies.ui.dialog.NavigableTree
import com.codingchili.bunnies.ui.dialog.auctionsFilterTree
import com.trello.rxlifecycle4.kotlin.bindToLifecycle

/**
 * Fragment used to show a list of links to a set of auctions based on status.
 */
class QuickLinksFragment : Fragment() {
    private val shared by activityViewModels<AuctionListViewModel>()
    private var service = AuctionService.instance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_quick_links, container, false)
        val list = fragment.findViewById<ListView>(R.id.list_view)
        val adapter = createAdapter(inflater, fragment)
        list.adapter = adapter
        refresh(adapter, auctionsFilterTree)
        return fragment
    }

    private fun onRowClickListener(leaf: NavigableTree, fragment: View): View.OnClickListener {
        return View.OnClickListener {
            fragment.findViewById<View>(R.id.progress_search).visibility =
                View.VISIBLE

            service.search(leaf.query()).bindToLifecycle(fragment).subscribe { auctions, e ->
                fragment.findViewById<View>(R.id.progress_search).visibility =
                    View.GONE

                if (e == null) {
                    if (auctions.isNotEmpty()) {
                        shared.list.value = auctions

                        requireActivity().title = getString(leaf.name)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                            .add(R.id.root, AuctionListFragment())
                            .addToBackStack(AuctionListFragment.TAG)
                            .commit()
                    } else {
                        AppToast.show(context, getString(R.string.no_auctions_in_category))
                    }
                } else {
                    AppToast.show(context, e.message)
                }
            }
        }
    }

    private fun createAdapter(
        inflater: LayoutInflater,
        fragment: View
    ): ArrayAdapter<NavigableTree> {
        return object :
            ArrayAdapter<NavigableTree>(requireContext(), R.layout.item_navigable_icon) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val leaf = getItem(position)!!
                val row = convertView ?: inflater.inflate(
                    R.layout.item_navigable_icon,
                    parent,
                    false
                )
                val title = row.findViewById<TextView>(R.id.item_text)
                val image = row.findViewById<ImageView>(R.id.item_icon)
                val next = row.findViewById<ImageView>(R.id.navigate_next)

                if (leaf is Divider) {
                    listOf(image, next).forEach { it.visibility = View.GONE }
                    title.text = getString(leaf.name)
                    row.background = null
                    row.isClickable = false
                } else {
                    title.text = getString(leaf.name)
                    image.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            leaf.resource!!,
                            null
                        )
                    )
                    row.setOnClickListener(onRowClickListener(leaf, fragment))
                }
                return row
            }
        }
    }

    private fun refresh(
        adapter: ArrayAdapter<NavigableTree>,
        items: Collection<NavigableTree>
    ) {
        adapter.clear()
        adapter.addAll(items)
        adapter.notifyDataSetChanged()
    }
}