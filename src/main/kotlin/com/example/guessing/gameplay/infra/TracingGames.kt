package com.example.guessing.gameplay.infra

import com.example.guessing.common.infra.DatabaseCall
import com.example.guessing.gameplay.Game
import com.example.guessing.gameplay.GameId
import com.example.guessing.gameplay.Games
import com.example.guessing.player.PlayerId
import org.http4k.events.Events
import org.http4k.filter.ZipkinTracesStorage
import org.http4k.filter.inChildSpan

class TracingGames(val events: Events, val games: Games) : Games {
    override fun save(game: Game) {
        trace("save")
        games.save(game)
    }

    override fun findByIdAndPlayerId(id: GameId, playerId: PlayerId): Game? {
        trace("find by id and player id")
        val existingGame = games.findByIdAndPlayerId(id, playerId)
        return existingGame
    }

    private fun trace(operation: String) {
        ZipkinTracesStorage.THREAD_LOCAL.inChildSpan {
            events(DatabaseCall("games", operation))
        }
    }
}