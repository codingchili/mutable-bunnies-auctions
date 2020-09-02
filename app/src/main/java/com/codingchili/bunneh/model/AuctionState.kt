package com.codingchili.bunneh.model

import com.codingchili.banking.model.Auction
import com.codingchili.bunneh.R
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

enum class AuctionState(val icon: Int, val color: Int, val string: Int) {
    // auction is active
    LEADING(R.drawable.icon_auction_leading, R.color.auction_lead, R.string.auction_leading), // the leading bid is by user.
    ACTIVE(R.drawable.icon_active, R.color.auction_active, R.string.auction_active),  // does not contain any bids by user.
    OVERBID(R.drawable.icon_auction_overbid, R.color.auction_overbid, R.string.auction_overbid), // user does not own the high bid.

    // auction is ended
    LOST(R.drawable.icon_auction_lost, R.color.auction_lost, R.string.auction_lost), // has a high bid by other than user.
    WON(R.drawable.icon_auction_won, R.color.auction_won, R.string.auction_won),  // has a high bid by user.
    END(R.drawable.icon_auction_end, R.color.auction_end, R.string.auction_end);   // does not contain any bids by user.

    fun interative(): Boolean {
        return listOf(ACTIVE, OVERBID).contains(this)
    }

    fun active(): Boolean {
        return listOf(LEADING, ACTIVE, OVERBID).contains(this)
    }

    fun end(): Boolean {
        return listOf(LOST, WON, END).contains(this)
    }

    companion object {
        fun fromAuction(auction: Auction, user: String): AuctionState {
            val active = ZonedDateTime.now().isBefore(
                ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(auction.end), ZoneId.systemDefault()
                )
            )
            return when {
                auction.bids.firstOrNull()?.owner == user -> if (active) LEADING else WON
                auction.bids.find { it.owner == user } != null -> if (active) OVERBID else LOST
                active -> ACTIVE
                else -> END
            }
        }
    }
}