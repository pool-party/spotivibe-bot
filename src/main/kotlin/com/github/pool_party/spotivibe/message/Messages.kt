package com.github.pool_party.spotivibe.message

import com.adamratzman.spotify.models.Track
import com.elbekD.bot.types.Message
import com.github.pool_party.spotivibe.escape
import com.github.pool_party.spotivibe.toLine

const val WRONG_USAGE = "Wrong usage"

const val INVALID_VOTERS = "Illegal voters number, expected an odd positive integer"

const val INVALID_URI = "Illegal Spotify ID/URI"

const val EMPTY_PLAYLIST = "Playlist is empty"

fun voteMessage(rounds: Int, currentRound: Int, first: Track, second: Track) =
    """
        Round of 1/$rounds \($currentRound/$rounds\):

        1. ${first.toLine()}
        2. ${second.toLine()}
    """.trimIndent()

fun votedMessage(previousMessage: Message, first: Track, second: Track, isFirst: Boolean): String {
    val firstLine = previousMessage.text?.takeWhile { it != '\n' }?.escape() ?: ""
    return "$firstLine\n\n" +
        if (isFirst) {
            """
                1. ${first.toLine()}
                ~2. ${second.toLine()}~
            """.trimIndent()
        } else {
            """
                ~1. ${first.toLine()}~
                2. ${second.toLine()}
            """.trimIndent()
        }
}

fun voteButtonMessage(isFirst: Boolean, voted: Int): String {
    val number = if (isFirst) """1️⃣""" else """2️⃣"""
    val votedNumber = if (voted != 0) " — $voted" else ""
    return "$number$votedNumber"
}
