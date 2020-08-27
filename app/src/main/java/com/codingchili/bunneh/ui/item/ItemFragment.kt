package com.codingchili.bunneh.ui.item

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.codingchili.bunneh.R
import com.codingchili.bunneh.ui.dialog.NumberInputDialog
import com.codingchili.bunneh.model.Item
import com.codingchili.bunneh.ui.dialog.Dialogs
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ItemFragment: Fragment() {
    private lateinit var item: Item

    fun load(item: Item): Fragment {
        this.item = item
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_item, container, false)

        requireActivity().title = item.name
        fragment.findViewById<TextView>(R.id.item_description).text = item.description
        fragment.findViewById<TextView>(R.id.item_stats).text = item.stats

        fragment.findViewById<MaterialButton>(R.id.button_sell).setOnClickListener {
            NumberInputDialog()
                .show(requireActivity().supportFragmentManager, Dialogs.TAG)
        }

        val quantity = fragment.findViewById<TextView>(R.id.item_quantity)
        if (item.quantity > 1) {
            quantity.text = "x" + item.quantity.toString()
        } else {
            quantity.visibility = View.GONE
        }

        Glide.with(requireContext())
            .load(getString(R.string.resources_host) + "/resources/gui/item/icon/${item.icon}")
            .into(fragment.findViewById(R.id.item_image))

        fragment.findViewById<FloatingActionButton>(R.id.back).setOnClickListener {
            requireActivity().onBackPressed()
        }

        setLabel(fragment.findViewById(R.id.item_slot), item.slot, getColorForItemType())
        setLabel(fragment.findViewById(R.id.item_type), item.type, R.color.type_default)
        setLabel(
            fragment.findViewById(R.id.item_rarity),
            item.rarity.toString(),
            item.rarity.resource
        )

        return fragment
    }

    private fun setLabel(view: TextView, value: String?, color: Int) {
        if (value != null) {
            val background = view.background.mutate() as GradientDrawable
            background.setColor(ContextCompat.getColor(requireContext(), color));
            view.text = value
            view.background = background
        } else {
            view.visibility = View.GONE
        }
    }

    private fun getColorForItemType() = when (item.slot) {
        "consumable" -> R.color.type_consumable
        "weapon" -> R.color.type_weapon
        "armor" -> R.color.type_armor
        "quest" -> R.color.type_quest
        else -> R.color.type_default
    }
}