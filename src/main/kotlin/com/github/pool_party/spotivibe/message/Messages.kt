package com.github.pool_party.spotivibe.message

import com.adamratzman.spotify.models.Track

fun Track.toLine(): String {
    val artists = artists.asSequence().map { it.name }.joinToString(", ")
    val text = "$artists — $name"
    return text.take(100)
}

fun Track.toUrl() = "https://open.spotify.com/track/$id"

const val WRONG_USAGE = "Wrong usage"

const val INVALID_VOTERS = "Illegal voters number, expected an odd positive integer"

const val INVALID_URI = "Illegal Spotify ID/URI"

const val EMPTY_PLAYLIST = "Playlist is empty"

fun voteMessage(rounds: Int, currentRound: Int) = "Round of 1/$rounds ($currentRound/$rounds)"
