package com.codingchili.bunneh.model

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

enum class AuctionState {
    // auction is active
    LEADING, // the leading bid is by user.
    ACTIVE,  // does not contain any bids by user.
    OVERBID, // user does not own the high bid.

    // auction is ended
    LOST, // has a high bid by other than user.
    WON,  // has a high bid by user.
    END;   // does not contain any bids by user.

    companion object {
        fun stateFromAuction(auction: Auction, user: User): AuctionState {
            val active = ZonedDateTime.now().isBefore(
                ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(auction.end), ZoneId.systemDefault()
                )
            )
            return when {
                auction.bids.isEmpty() -> if (active) ACTIVE else END
                auction.bids.first().owner == user -> if (active) LEADING else WON
                auction.bids.find { it.owner == user } != null -> if (active) OVERBID else LOST
                active -> ACTIVE
                else -> END
            }
        }
    }
}