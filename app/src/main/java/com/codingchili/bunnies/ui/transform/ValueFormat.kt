package com.codingchili.bunnies.ui.transform

import java.text.NumberFormat

/**
 * Formats the given value using decimal separators and appends a currency icon.
 */
fun formatValue(value: Int): String {
    return "${NumberFormat.getInstance().format(value)} Ξ"
}

/**
 * Formats the given value using decimal separators, ignoring any separators already present.
 */
fun formatValue(value: String): String {
    return if (value.isEmpty())
        value
    else NumberFormat.getInstance().format(
        Integer.parseInt(value.replace(",", ""))
    )
}