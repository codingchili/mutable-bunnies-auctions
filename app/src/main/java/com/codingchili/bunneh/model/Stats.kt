package com.codingchili.bunneh.model

class Stats : HashMap<String, Int>() {

    init {
        put("Dexterity", 4)
        put("Strength", 4)
    }

    override fun toString(): String {
        return map { "${if (it.value > 0) "+${it.value}" else it.value} ${it.key}" }
            .joinToString("\n")
    }
}