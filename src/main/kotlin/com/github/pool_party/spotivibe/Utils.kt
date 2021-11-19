package com.github.pool_party.spotivibe

import com.adamratzman.spotify.models.Track

fun String.escapeParentheses() = replace("[()]".toRegex()) { "\\${it.groups[0]!!.value}" }

fun Track.toLine(): String {
    val artists = artists.asSequence().map { it.name }.joinToString(", ")
    val text = "$artists - $name".escapeParentheses()
    val link = "https://open.spotify.com/track/$id"
    return "[$text]($link)"
}
