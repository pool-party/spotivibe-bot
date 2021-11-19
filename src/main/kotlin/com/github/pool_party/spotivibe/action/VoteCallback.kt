package com.github.pool_party.spotivibe.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.flume.interaction.callback.AbstractCallbackDispatcher
import com.github.pool_party.flume.interaction.callback.Callback
import com.github.pool_party.flume.utils.editMessageReplyMarkupLogging
import com.github.pool_party.flume.utils.editMessageTextLogging
import com.github.pool_party.flume.utils.toMarkUp
import com.github.pool_party.spotivibe.chatInfos
import com.github.pool_party.spotivibe.message.votedMessage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.math.max

@Serializable
sealed class CallbackData {

    val encoded: String
        get() = ProtoBuf.encodeToByteArray(this).joinToString("") { it.toInt().toChar().toString() }

    companion object {
        fun of(string: String) =
            ProtoBuf.decodeFromByteArray<CallbackData>(string.map { it.code.toByte() }.toByteArray())
    }
}

@Serializable
@SerialName("vote")
data class VoteCallbackData(val isFirst: Boolean, val chatId: Long) : CallbackData()

class CallbackDispatcher(vararg callbacks: Callback<CallbackData>) :
    AbstractCallbackDispatcher<CallbackData>(callbacks.toList()) {

    override fun getCallbackData(data: String): CallbackData = CallbackData.of(data)
}

class VoteCallback : Callback<CallbackData> {

    override val callbackDataKClass = VoteCallbackData::class

    private val mutex = Mutex()

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val data = callbackData as? VoteCallbackData ?: return

        val chatId = data.chatId

        val chatInfo = chatInfos[chatId] ?: return
        val current = chatInfo.currentList
        val voted = chatInfo.voted

        val (first, second) = current

        answerCallbackQuery(callbackQuery.id)

        mutex.withLock {
            val fromId = callbackQuery.from.id
            val previous = chatInfo.voted[fromId]
            chatInfo.voted[fromId] = data.isFirst

            if (previous == data.isFirst) return

            val forFirst = voted.values.asSequence().filter { it }.count()
            val forSecond = voted.values.asSequence().filter { !it }.count()

            if (max(forFirst, forSecond) <= chatInfo.voters / 2) {
                callbackQuery.message?.let { message ->
                    editMessageReplyMarkupLogging(
                        chatId,
                        message.message_id,
                        listOf(
                            voteButton(true, forFirst, chatId),
                            voteButton(false, forSecond, chatId),
                        ).toMarkUp(),
                    )
                }
                return
            }

            current.removeFirst()
            current.removeFirst()
            chatInfo.voted.clear()

            val isFirst = forFirst >= forSecond

            chatInfo.nextList.add(if (isFirst) first else second)

            callbackQuery.message?.let { message ->
                editMessageTextLogging(
                    chatId,
                    message.message_id,
                    votedMessage(message, first, second, isFirst),
                    disableWebPagePreview = true,
                )
            }

            vote(chatId)
        }
    }
}
