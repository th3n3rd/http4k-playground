package com.example

import java.util.Collections.synchronizedMap

interface Games {
    fun save(game: Game)
    fun findById(id: GameId): Game?
}

class InMemoryGames: Games {
    private val gamesById = synchronizedMap(mutableMapOf<GameId, Game>())

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
