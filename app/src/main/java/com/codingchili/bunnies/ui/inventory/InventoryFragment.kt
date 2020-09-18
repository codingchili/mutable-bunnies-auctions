package com.codingchili.bunnies.ui.inventory

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
import com.codingchili.banking.model.Inventory
import com.codingchili.banking.model.Item
import com.codingchili.bunnies.R
import com.codingchili.bunnies.api.AuctionService
import com.codingchili.bunnies.ui.AppToast
import com.codingchili.bunnies.ui.dialog.*
import com.codingchili.bunnies.ui.item.ItemFragment
import com.codingchili.bunnies.ui.item.ItemViewModel
import com.codingchili.bunnies.ui.transform.Sorter
import com.codingchili.bunnies.ui.transform.formatValue
import com.codingchili.bunnies.ui.transform.itemGridAdapter
import com.trello.rxlifecycle4.kotlin.bindToLifecycle
import java.util.function.Consumer

/**
 * The inventory fragment is used to show the users inventory and
 * available funds. From here items can be shown in the item fragment which
 * allows further interaction.
 */
class InventoryFragment : Fragment() {
    private val service = AuctionService.instance
    private val model by activityViewModels<InventoryViewModel>()
    private val shared by activityViewModels<ItemViewModel>()
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
        val progress = fragment.findViewById<ProgressBar>(R.id.progress_search)

        // show information dialog explaining how funds are displayed.
        fragment.findViewById<View>(R.id.currency_container).setOnClickListener {
            InformationDialog(R.string.liquidity_title, R.string.liquidity_text)
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        // hook up sorting for the sort buttion, reuses the sorter and dialog.
        fragment.findViewById<View>(R.id.sort).setOnClickListener {
            NavigableTreeDialog(
                R.string.dialog_sort,
                sortItemsTree,
                Consumer<NavigableTree> { leaf ->
                    sorter.ascending = leaf.name == R.string.nav_ascending
                    sorter.setMethodByName(leaf.parent!!.name)
                    model.inventory.value = applySort(model.inventory.value!!)
                }
            ).show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        val adapter = itemGridAdapter(
            this,
            inflater,
            Consumer<Item> { item ->
                this.shared.item.value = item
                requireActivity().title = item.name
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.root, ItemFragment())
                    .addToBackStack(ItemFragment.TAG)
                    .commit()
            })
        grid.adapter = adapter

        // observe changes to the inventory and update the grid.
        model.inventory.observe(viewLifecycleOwner, Observer {
            funds.text = formatValue(it.funds)
            liquidity.text = formatValue(it.liquidity)

            adapter.clear()
            adapter.addAll(applySort(model.inventory.value!!).items)
            adapter.notifyDataSetChanged()
        })

        progress.visibility = View.VISIBLE
        service.inventory().bindToLifecycle(fragment).subscribe { response, e ->
            if (e == null) {
                model.inventory.value = response
            } else {
                AppToast.show(context, e.message)
            }
            progress.visibility = View.GONE
        }
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