package com.github.pool_party.spotivibe.action

import com.elbekD.bot.Bot
import com.elbekD.bot.http.await
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.flume.utils.logging
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.flume.utils.toMarkUp
import com.github.pool_party.spotivibe.ChatInfo
import com.github.pool_party.spotivibe.message.toLine
import com.github.pool_party.spotivibe.message.toUrl
import com.github.pool_party.spotivibe.message.voteMessage
import com.github.pool_party.spotivibe.polls
import com.github.pool_party.spotivibe.retrying

suspend fun Bot.vote(chatInfo: ChatInfo) {
    val chatId = chatInfo.chatId

    if (chatInfo.currentList.isEmpty()) {
        chatInfo.currentList = chatInfo.nextList
        chatInfo.nextList = mutableListOf()
    }

    val current = chatInfo.currentList
    val next = chatInfo.nextList

    if (current.size == 1) {
        retrying { sendMessageLogging(chatId, "Winner: *${current[0].toLine()}*") }
        return
    }

    val options = current.take(2)

    val message = retrying {
        sendPoll(
            chatId,
            voteMessage(next.size + current.size / 2, next.size + 1),
            options.map { it.toLine() },
            anonymous = false,
            type = "regular",
            allowsMultipleAnswers = false,
            markup = options.asSequence()
                .withIndex()
                .map { InlineKeyboardButton("${it.index + 1}", it.value.toUrl()) }
                .toList()
                .toMarkUp()
        )
            .logging()
            .await()
    }

    chatInfo.pollMessageId = message.message_id
    message.poll?.let { polls[it.id] = chatInfo }
}
