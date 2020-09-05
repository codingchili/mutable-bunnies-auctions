package com.codingchili.bunnies.model

import com.codingchili.banking.model.Auction
import com.codingchili.bunnies.R
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Representation of the state of an auction, used for mapping auctions to an UI representation.
 *
 * Auctions has six possible states,
 *
 * Three states in which the auction is still active;
 * ACTIVE - the auction is created and no bids are placed.
 * LEADING - the user has placed the highest bid on the auction.
 * OVERBID - the user has placed a bid at least once and another user placed a higher bid.
 *
 * Three other states for when the auction has completed;
 * LOST - the user has placed a bid, but it was not the winning bid.
 * WON - the user has placed a bid and it is the winning bid.
 * END - the auction has completed, the user did not place any bids.
 *
 * Note that users may not place any bids at all, so they may only see ACTIVE - END
 * for their own auctions.
 *
 * The state class contains a few helper methods to simplify state checking.
 */
enum class AuctionState(val icon: Int, val color: Int, val string: Int) {
    // auction is active
    LEADING(
        R.drawable.icon_auction_leading,
        R.color.auction_lead,
        R.string.auction_leading
    ), // the leading bid is by user.
    ACTIVE(
        R.drawable.icon_active,
        R.color.auction_active,
        R.string.auction_active
    ),  // does not contain any bids by user.
    OVERBID(
        R.drawable.icon_auction_overbid,
        R.color.auction_overbid,
        R.string.auction_overbid
    ), // user does not own the high bid.

    // auction is ended
    LOST(
        R.drawable.icon_auction_lost,
        R.color.auction_lost,
        R.string.auction_lost
    ), // has a high bid by other than user.
    WON(
        R.drawable.icon_auction_won,
        R.color.auction_won,
        R.string.auction_won
    ),  // has a high bid by user.
    END(
        R.drawable.icon_auction_end,
        R.color.auction_end,
        R.string.auction_end
    );   // does not contain any bids by user.

    /**
     * @return true if the auction is interactive; this means that the auction is active
     * and bids may be placed. Note that an auction may be interactive, but not by the
     * current user - this method does not consider the currently logged in user.
     */
    fun interative(): Boolean {
        return listOf(ACTIVE, OVERBID).contains(this)
    }

    /**
     * @return true if this auction is still active; the auction has not come to an end.
     */
    fun active(): Boolean {
        return listOf(LEADING, ACTIVE, OVERBID).contains(this)
    }

    /**
     * @return true; if this auction is no longer active.
     */
    fun end(): Boolean {
        return listOf(LOST, WON, END).contains(this)
    }

    companion object {
        /**
         * Creates a state object from the given auction and user.
         * @param auction the auction to determine the state of.
         * @param user the user to check the auctions bid against.
         * @return an auction state object.
         */
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