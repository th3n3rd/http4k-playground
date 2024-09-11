package com.example.guessing.gameplay.infra

import com.example.guessing.gameplay.Game
import com.example.guessing.gameplay.GameId
import com.example.guessing.gameplay.Games
import com.example.guessing.player.PlayerId
import java.util.concurrent.ConcurrentHashMap

fun Games.Companion.InMemory() = InMemoryGames()

class InMemoryGames: Games {
    private val gamesById = ConcurrentHashMap<GameId, Game>()

    override fun save(game: Game) {
        gamesById[game.id] = game
    }

    override fun findByIdAndPlayerId(id: GameId, playerId: PlayerId): Game? {
        return gamesById[id]?.takeIf { it.ownedBy(playerId) }
    }

    fun findAll(): List<Game> {
        return gamesById.values.toList()
    }
}
