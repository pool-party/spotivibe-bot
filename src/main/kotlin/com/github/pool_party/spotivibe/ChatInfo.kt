package com.github.pool_party.spotivibe

import com.adamratzman.spotify.models.Track

val chatInfos = mutableMapOf<Long, ChatInfo>()

data class ChatInfo(
    val voters: Int,
    var currentList: MutableList<Track>,
    var nextList: MutableList<Track> = mutableListOf(),
    val previews: MutableList<Int> = mutableListOf(),
    val voted: MutableMap<Int, Boolean> = mutableMapOf()
)
