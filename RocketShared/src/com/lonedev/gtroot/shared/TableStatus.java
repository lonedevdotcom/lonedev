package com.lonedev.gtroot.shared;

/**
 * Intended to show the various "statuses" that a table can be in. As players
 * make their decisions (or come and go), this enum should have a suitable
 * status to identify what's going on. The table can then move to these
 * statuses and explain where we are at any given moment.
 *
 * @author Richard Hawkes
 */
public enum TableStatus {
    EMPTY,
    AWAITING_MORE_PLAYERS, // Nothing to do here
    WAITING_FOR_PLAYER_ROLL, // Send messge to channel. Clients should display player 1. Player 1 should have the "roll button".
//    HANDLING_PLAYER_ROLL_REQUEST, // Roll a random dice number, and inform everyone on the channel what it was
    WAITING_FOR_PLAYER_ROLL_RESPONSE, // Waiting for the player to respond to their dice roll. Nothing to do here.
//    HANDLING_PLAYER_ROLL_RESPONSE, // This involves updating the players modules, and informing the channel.
}
