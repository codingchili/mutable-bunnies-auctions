package com.codingchili.bunnies.ui.transform

import android.widget.Chronometer
import com.codingchili.banking.model.Auction
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Stream

/**
 * Sets up a chronometer to track the ending of the given auction. The listener is invoked
 * on each tick of the chronometer. The chronometer setup is quite verbose so its placed
 * here for reuse.
 */
fun setupChronometerFromAuction(chronometer: Chronometer, auction: Auction, listener: Runnable? = null) {
    chronometer.base = auction.end
    chronometer.isCountDown = true
    chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener {
        chronoCountdownHandler.onChronometerTick(it)
        listener?.run()
    }
    chronometer.start()
}

private val chronoCountdownHandler = Chronometer.OnChronometerTickListener { chronometer ->
    val now = ZonedDateTime.now()
    var then =
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(chronometer!!.base),
            ZoneId.systemDefault()
        )

    // displays at most two of the biggest units that are nonzero.
    // example, 3d 21h or 43m 20s short and readable.
    chronometer.text =
        Stream.of(ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS)
            .map {
                val diff = it.between(now, then)
                then = it.addTo(then, -diff)
                Pair<Long, ChronoUnit>(diff, it)
            }
            .filter { it.first > 0 }
            .map {
                " ${it.first} ${it.second.name.toLowerCase(Locale.ROOT)}"
            }
            .limit(2)
            .toArray()
            .joinToString(",")
}