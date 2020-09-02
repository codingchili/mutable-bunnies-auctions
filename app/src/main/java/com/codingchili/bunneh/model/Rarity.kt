package com.codingchili.bunneh.model

import com.codingchili.banking.model.Item
import com.codingchili.bunneh.R

enum class Rarity(val resource: Int) {
    common(R.color.quality_common),
    uncommon(R.color.quality_uncommon),
    rare(R.color.quality_rare),
    epic(R.color.quality_epic),
    legendary(R.color.quality_legendary),
    mythic(R.color.quality_mythic);

    companion object {
        fun resource(item: Item): Int {
            return Rarity.valueOf(item.rarity.name).resource
        }
    }
}