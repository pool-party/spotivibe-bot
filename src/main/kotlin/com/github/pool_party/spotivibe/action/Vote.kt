package com.github.pool_party.spotivibe.action

import com.elbekD.bot.Bot
import com.elbekD.bot.http.await
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.flume.utils.deleteMessageLogging
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.flume.utils.toMarkUp
import com.github.pool_party.spotivibe.chatInfos
import com.github.pool_party.spotivibe.message.voteButtonMessage
import com.github.pool_party.spotivibe.message.voteMessage
import com.github.pool_party.spotivibe.toLine

fun voteButton(isFirst: Boolean, voted: Int, chatId: Long) =
    InlineKeyboardButton(voteButtonMessage(isFirst, voted), callback_data = VoteCallbackData(isFirst, chatId).encoded)

suspend fun Bot.vote(chatId: Long) {
    val chatInfo = chatInfos[chatId] ?: return
    chatInfo.previews.forEach { deleteMessageLogging(chatId, it) }
    chatInfo.previews.clear()

    if (chatInfo.currentList.isEmpty()) {
        chatInfo.currentList = chatInfo.nextList
        chatInfo.nextList = mutableListOf()
    }

    val current = chatInfo.currentList
    val next = chatInfo.nextList

    if (current.size == 1) {
        sendMessageLogging(chatId, "Winner: *${current[0].toLine()}*")
        chatInfos.remove(chatId)
        return
    }

    val (first, second) = current

    sendMessageLogging(
        chatId,
        voteMessage(next.size + current.size / 2, next.size + 1, first, second),
        markup = listOf(
            voteButton(true, 0, chatId),
            voteButton(false, 0, chatId),
        ).toMarkUp(),
        disableWebPagePreview = true,
    ).await()
//
//    val previews = listOf(first, second).mapNotNull { it to (it.previewUrl ?: return@mapNotNull null) }
//
//    when (previews.size) {
//        1 -> {
//            val (track, preview) = previews[0]
//            val message = sendAudio(
//                chatId,
//                audio = preview,
//                duration = 30,
//                performer = track.artists.joinToString(", "), // TODO
//                title = track.name
//            ).await()
//            chatInfo.previews.add(message.message_id)
//        }
//        2 -> {
//            val message = sendMediaGroup(
//                chatId,
//                previews.map {
//                    mediaAudio(
//                        it.second,
//                        duration = 30,
//                        performer = "a", // it.first.artists.joinToString(", "),
//                        title = "b", // it.first.name
//                    )
//                }
//            ).await()
//            chatInfo.previews.addAll(message.map { it.message_id })
//        }
//    }
}
