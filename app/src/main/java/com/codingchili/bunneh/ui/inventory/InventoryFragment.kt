package com.codingchili.bunneh.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.codingchili.bunneh.R
import com.codingchili.bunneh.api.AuctionService
import com.codingchili.bunneh.model.Inventory
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.ui.dialog.*
import com.codingchili.bunneh.ui.item.ItemFragment
import com.codingchili.bunneh.ui.transform.Sorter
import com.codingchili.bunneh.ui.transform.formatValue
import com.codingchili.bunneh.ui.transform.itemGridAdapter
import java.util.function.Consumer

class InventoryFragment() : Fragment() {
    private val service = AuctionService.instance
    private var inventory = MutableLiveData<Inventory>(Inventory())
    private val sorter = Sorter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_inventory, container, false)
        val grid = fragment.findViewById<GridView>(R.id.inventory_items)
        val funds = fragment.findViewById<TextView>(R.id.currency_total)
        val liquidity = fragment.findViewById<TextView>(R.id.currency_available)

        fragment.findViewById<View>(R.id.currency_container).setOnClickListener {
            InformationDialog(R.string.liquidity_title, R.string.liquidity_text)
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        fragment.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog(
                "Sort",
                searchFilterTree,
                Consumer<NavigableTree> { leaf ->
                    sorter.ascending = leaf.name == getString(R.string.sort_ascending)
                    sorter.setMethodByName(requireContext(), leaf.parent!!.name)
                    inventory.value = applySort(inventory.value!!)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        val adapter = itemGridAdapter(
            this,
            inflater,
            Consumer<Item> { item ->
                requireActivity().title = item.name
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.root, ItemFragment().load(item))
                    .addToBackStack(ItemFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter

        inventory.observe(viewLifecycleOwner, Observer {
            funds.text = formatValue(it.funds)
            liquidity.text = formatValue(it.liquidity)

            adapter.clear()
            adapter.addAll(applySort(inventory.value!!).items)
            adapter.notifyDataSetChanged()
        })

        service.inventory().subscribe { inventory.value = it }
        return fragment
    }

    private fun applySort(inventory: Inventory): Inventory {
        return Inventory(
            items = sorter.sortItems(inventory.items),
            funds = inventory.funds,
            liquidity = inventory.liquidity
        )
    }
}