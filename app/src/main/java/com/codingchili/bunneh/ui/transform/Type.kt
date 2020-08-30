package com.codingchili.bunneh.ui.transform

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.codingchili.bunneh.R
import com.codingchili.bunneh.model.Item

/**
 * Handles display of item types as tags.
 */
class Type {
    companion object {
        val consumable = "consumable"
        val weapon = "weapon"
        val armor = "armor"
        val quest = "quest"

        fun updateLabels(view: View, item: Item) {
            setLabel(
                view.context,
                view.findViewById(R.id.item_slot),
                item.slot,
                getColorForItemSlot(item)
            )
            setLabel(
                view.context,
                view.findViewById(R.id.item_type),
                item.type,
                R.color.type_default
            )
            setLabel(
                view.context,
                view.findViewById(R.id.item_rarity),
                item.rarity.toString(),
                item.rarity.resource
            )
        }

        private fun getColorForItemSlot(item: Item) = when (item.slot) {
            consumable -> R.color.type_consumable
            weapon -> R.color.type_weapon
            armor -> R.color.type_armor
            quest -> R.color.type_quest
            else -> R.color.type_default
        }

        private fun setLabel(context: Context, view: TextView, value: String?, color: Int) {
            if (value != null) {
                val background = view.background.mutate() as GradientDrawable
                background.setColor(ContextCompat.getColor(context, color));
                view.text = value
                view.background = background
            } else {
                view.visibility = View.GONE
            }
        }
    }
}