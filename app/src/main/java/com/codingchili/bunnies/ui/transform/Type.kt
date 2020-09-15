package com.codingchili.bunnies.ui.transform

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.codingchili.banking.model.Item
import com.codingchili.bunnies.R
import com.codingchili.bunnies.model.Rarity

/**
 * Handles display of item types as tags.
 */
class Type {
    companion object {
        const val consumable = "consumable"
        const val weapon = "weapon"
        const val armor = "armor"
        const val quest = "quest"

        /**
         * Updates labels in the given view with type information from the given item.
         * @param view a reference to the view containing labels.
         * @param item the item which contains the types to display labels for.
         */
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
                getColorForItemType(item)
            )
            setLabel(
                view.context,
                view.findViewById(R.id.item_rarity),
                item.rarity.toString(),
                Rarity.resource(item)
            )
        }

        private fun getColorForItemType(item: Item) = when(item.type) {
            consumable -> R.color.type_consumable
            else -> R.color.type_default
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