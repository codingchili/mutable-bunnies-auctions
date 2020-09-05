package com.codingchili.bunnies.model

import com.codingchili.banking.model.Item
import com.codingchili.bunnies.R

/**
 * Representation of item rarity, mapped to color resource.
 */
enum class Rarity(val resource: Int) {
    common(R.color.quality_common),
    uncommon(R.color.quality_uncommon),
    rare(R.color.quality_rare),
    epic(R.color.quality_epic),
    legendary(R.color.quality_legendary),
    mythic(R.color.quality_mythic);

    companion object {
        /**
         * Determines which resource to use based on the given item.
         * @param item the item to deduce the color resource from.
         * @return the resource id of the color resource matching the given items rarity.
         */
        fun resource(item: Item): Int {
            return Rarity.valueOf(item.rarity.name).resource
        }
    }
}