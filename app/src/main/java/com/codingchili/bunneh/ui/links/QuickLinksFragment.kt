package com.codingchili.bunneh.ui.links

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.LocalAuctionService
import com.codingchili.bunneh.ui.dialog.Divider
import com.codingchili.bunneh.ui.dialog.NavigableTree
import com.codingchili.bunneh.ui.dialog.auctionsFilterTree

/**
 * Fragment used to show a list of links to a set of auctions.
 */
class QuickLinksFragment : Fragment() {
    private var service = LocalAuctionService.instance

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

            service.search(leaf.name).subscribe { auctions, e ->

                fragment.findViewById<View>(R.id.progress_search).visibility =
                    View.GONE

                if (e == null) {
                    if (auctions.isNotEmpty()) {
                        requireActivity().title = leaf.name
                        requireActivity().supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                            .add(R.id.root, AuctionListFragment(auctions).icon(leaf.icon!!))
                            .addToBackStack(AuctionListFragment.TAG)
                            .commit()
                    } else {
                        toast("No auctions in category.")
                    }
                } else {
                    toast("Error: " + e.message)
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
                    title.text = leaf.name
                    row.background = null
                } else {
                    title.text = leaf.name
                    image.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            leaf.icon!!,
                            null
                        )
                    )
                    row.setOnClickListener(onRowClickListener(leaf, fragment))
                }
                return row
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
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