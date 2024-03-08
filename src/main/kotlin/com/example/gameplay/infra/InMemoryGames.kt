package com.example.gameplay.infra

import com.example.gameplay.Game
import com.example.gameplay.GameId
import com.example.gameplay.Games
import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun Games.Companion.InMemory() = InMemoryGames()

class InMemoryGames: Games {
    private val gamesById = ConcurrentHashMap<GameId, Game>()

    override fun save(game: Game) {
        gamesById[game.id] = game
    }

    override fun findById(id: GameId): Game? {
        return gamesById[id]
    }

    fun findAll(): List<Game> {
        return gamesById.values.toList()
    }
}
