package com.codingchili.bunneh.model

import com.codingchili.bunneh.R

enum class ItemRarity(val resource: Int) {
    common(R.color.quality_common),
    uncommon(R.color.quality_uncommon),
    rare(R.color.quality_rare),
    epic(R.color.quality_epic),
    legendary(R.color.quality_legendary),
    mythic(R.color.quality_mythic);
}