package com.github.pool_party.spotivibe

import com.elbekD.bot.http.TelegramApiError
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

suspend fun <T> retrying(function: suspend () -> T): T {
    while (true) {
        try {
            return function()
        } catch (e: TelegramApiError) {
            val prefix = "Error code: 429\nDescription: Too Many Requests: retry after "
            val message = e.message ?: throw e
            if (!message.startsWith(prefix)) throw e
            val interval = message.removePrefix(prefix).toIntOrNull() ?: throw e

            delay((interval + 3).seconds)
        }
    }
}
