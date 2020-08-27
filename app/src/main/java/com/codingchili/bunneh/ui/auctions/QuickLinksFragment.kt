package com.codingchili.bunneh.ui.auctions

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
import com.codingchili.bunneh.R
import com.codingchili.bunneh.model.Auction
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.model.ItemRarity
import com.codingchili.bunneh.ui.auction.AuctionFragment
import com.codingchili.bunneh.ui.dialog.Divider
import com.codingchili.bunneh.ui.dialog.NavigableTree
import com.codingchili.bunneh.ui.dialog.auctionsFilterTree

class QuickLinksFragment : Fragment() {
    private var auctions = ArrayList<Auction>() // use search to populate

    init {
        auctions.add(
            Auction(
                bid = 825000,
                item = Item(
                    icon = "flaming_stick.png",
                    rarity = ItemRarity.legendary,
                    quantity = 1,
                    title = "Flaming Stick +4",
                    slot = "weapon",
                    type = "staff"
                )
            )
        )
        for (i in 0..8) {
            auctions.addAll(auctions)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_quick_links, container, false)
        val list = fragment.findViewById<ListView>(R.id.list_view)

        val adapter =
            object : ArrayAdapter<NavigableTree>(requireContext(), R.layout.item_navigable_icon) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val node = getItem(position)!!
                    val row = convertView ?: inflater.inflate(
                        R.layout.item_navigable_icon,
                        parent,
                        false
                    )
                    val title = row.findViewById<TextView>(R.id.item_text)
                    val image = row.findViewById<ImageView>(R.id.item_icon)
                    val next = row.findViewById<ImageView>(R.id.navigate_next)

                    if (node is Divider) {
                        listOf(image, next).forEach { it.visibility = View.GONE }
                        title.text = node.name
                        row.background = null
                    } else {
                        title.text = node.name
                        image.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                node.icon!!,
                                null
                            )
                        )
                        row.setOnClickListener {
                            requireActivity().supportFragmentManager.beginTransaction()
                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
                                .add(R.id.root, AuctionListFragment(auctions))
                                .addToBackStack(AuctionListFragment.TAG)
                                .commit()
                        }
                    }
                    return row
                }
            }
        list.adapter = adapter
        refresh(adapter, auctionsFilterTree)
        return fragment
    }

    private fun refresh(adapter: ArrayAdapter<NavigableTree>, items: Collection<NavigableTree>) {
        adapter.clear()
        adapter.addAll(items)
        adapter.notifyDataSetChanged()
    }
}