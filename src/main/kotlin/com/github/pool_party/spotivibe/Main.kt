package com.github.pool_party.spotivibe

import com.github.pool_party.flume.bot.BotBuilder
import com.github.pool_party.spotivibe.action.CallbackDispatcher
import com.github.pool_party.spotivibe.action.ContestCommand
import com.github.pool_party.spotivibe.action.VoteCallback

fun main() {
    val botBuilder = BotBuilder(Configuration).apply {
        interactions = listOf(listOf(ContestCommand()), listOf(CallbackDispatcher(VoteCallback())))
    }

    botBuilder.start()
}
