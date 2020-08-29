package com.codingchili.bunneh.ui.transform

import android.widget.Chronometer
import com.codingchili.bunneh.model.Auction
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Stream

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

    chronometer.text =
        Stream.of(ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS)
            .map {
                val diff = it.between(now, then)
                then = it.addTo(then, -diff)
                Pair<Long, ChronoUnit>(diff, it)
            }
            .filter { it.first > 0 }
            .map {
                " ${it.first} ${it.second.name.toLowerCase(Locale.getDefault())}"
            }
            .limit(2)
            .toArray()
            .joinToString(",")
}