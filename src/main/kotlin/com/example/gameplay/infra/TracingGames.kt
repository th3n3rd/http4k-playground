package com.example.gameplay.infra

import com.example.common.infra.DatabaseCall
import com.example.gameplay.Game
import com.example.gameplay.GameId
import com.example.gameplay.Games
import com.example.player.PlayerId
import org.http4k.events.Events

class TracingGames(val events: Events, val games: Games) : Games {
    override fun save(game: Game) {
        events(DatabaseCall("games", "save"))
        games.save(game)
    }

    override fun findByIdAndPlayerId(id: GameId, playerId: PlayerId): Game? {
        events(DatabaseCall("games", "find by id and player id"))
        val existingGame = games.findByIdAndPlayerId(id, playerId)
        return existingGame
    }
}