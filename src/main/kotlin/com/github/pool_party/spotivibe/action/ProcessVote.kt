package com.github.pool_party.spotivibe.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.PollAnswer
import com.github.pool_party.flume.interaction.Interaction
import com.github.pool_party.spotivibe.polls
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.lang.Integer.max

class ProcessVote : Interaction {

    private val mutex = Mutex()

    private val logger = KotlinLogging.logger { }

    override val usages = listOf<String>()

    override fun apply(bot: Bot) {
        bot.onAnyUpdate {
            val pollAnswer = it.poll_answer ?: return@onAnyUpdate

            logger.info("New poll answer: ${pollAnswer.user.username} votes for ${pollAnswer.option_ids}")
            bot.process(pollAnswer)
        }
    }

    private suspend fun Bot.process(pollAnswer: PollAnswer) {
        val chatInfo = polls[pollAnswer.poll_id] ?: return

        var isFirst = false

        mutex.withLock {
            val voted = chatInfo.voted

            when (pollAnswer.option_ids.size) {
                0 -> voted.remove(pollAnswer.user.id)
                1 -> voted[pollAnswer.user.id] = pollAnswer.option_ids[0] == 0
                else -> throw IllegalStateException("""Multiple vote?.. 🤨""")
            }

            val forFirst = voted.values.count { it }
            val forSecond = voted.values.count { !it }

            if (max(forFirst, forSecond) != chatInfo.voters / 2 + 1) return

            polls.remove(pollAnswer.poll_id)
            isFirst = forFirst >= forSecond
        }

        stopPoll(chatInfo.chatId, chatInfo.pollMessageId!!)
        chatInfo.voted.clear()

        val currentList = chatInfo.currentList
        val first = currentList.removeFirst()
        val second = currentList.removeFirst()

        chatInfo.nextList.add(if (isFirst) first else second)
        vote(chatInfo)
    }
}
