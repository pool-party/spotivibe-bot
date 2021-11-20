package com.github.pool_party.spotivibe

import com.adamratzman.spotify.models.Track

val polls = mutableMapOf<String, ChatInfo>()

data class ChatInfo(
    val chatId: Long,
    val voters: Int,
    var currentList: MutableList<Track>,
    var nextList: MutableList<Track> = mutableListOf(),
    var voted: MutableMap<Int, Boolean> = mutableMapOf(),
    var pollMessageId: Int? = null,
)
