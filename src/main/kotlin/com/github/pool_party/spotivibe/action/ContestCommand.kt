package com.github.pool_party.spotivibe.action

import com.adamratzman.spotify.models.SpotifyUriException
import com.adamratzman.spotify.spotifyAppApi
import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.flume.interaction.command.AbstractCommand
import com.github.pool_party.flume.utils.chatId
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.spotivibe.ChatInfo
import com.github.pool_party.spotivibe.Configuration
import com.github.pool_party.spotivibe.chatInfos
import com.github.pool_party.spotivibe.message.EMPTY_PLAYLIST
import com.github.pool_party.spotivibe.message.INVALID_URI
import com.github.pool_party.spotivibe.message.INVALID_VOTERS
import com.github.pool_party.spotivibe.message.WRONG_USAGE
import kotlin.math.roundToInt
import kotlin.math.log2
import kotlin.math.floor
import kotlin.math.pow

class ContestCommand :
    AbstractCommand(
        "contest",
        "run a contest",
        "run a contest",
        listOf("playlist", "run a contest"),
        listOf("playlist", "number of voters", "run a contest in a group"),
        saveCase = true
    ) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        var (voters, playlistId) = when (args.size) {
            1 -> 1 to args[0]
            2 -> {
                val voters = args[1].toIntOrNull()
                if (voters == null || voters <= 0 || voters % 2 != 1) {
                    sendMessageLogging(chatId, INVALID_VOTERS)
                    return
                }
                voters to args[0]
            }
            else -> {
                sendMessageLogging(chatId, WRONG_USAGE)
                return
            }
        }

        val regex = "https://open.spotify.com/playlist/(\\w+)?.*".toRegex()
        val matchResult = regex.matchEntire(playlistId)

        playlistId = matchResult?.groups?.get(1)?.value ?: playlistId

        val api = spotifyAppApi(Configuration.CLIENT_ID, Configuration.CLIENT_SECRET).build()

        val playlist = try {
            api.playlists.getPlaylist(playlistId)
        } catch (e: SpotifyUriException) {
            sendMessageLogging(chatId, INVALID_URI)
            return
        }

        if (playlist == null) {
            sendMessageLogging(chatId, INVALID_URI)
            return
        }

        val tracks = playlist.tracks.getAllItemsNotNull().mapNotNull { it.track?.asTrack }

        if (tracks.isEmpty()) {
            sendMessageLogging(chatId, EMPTY_PLAYLIST)
            return
        }

        val pow2 = 2.0.pow(floor(log2(tracks.size.toDouble()))).roundToInt()
        chatInfos[chatId] = ChatInfo(voters, tracks.asSequence().shuffled().take(pow2).toMutableList())

        vote(chatId)
    }
}
