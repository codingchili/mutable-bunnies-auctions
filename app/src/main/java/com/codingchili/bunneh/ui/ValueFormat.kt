package com.codingchili.bunneh.ui

import java.text.NumberFormat

fun formatValue(value: Int): String {
    return "${NumberFormat.getInstance().format(value)} Ξ"
}
