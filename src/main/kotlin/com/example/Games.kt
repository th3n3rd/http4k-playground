package com.example

import java.util.concurrent.CopyOnWriteArrayList

interface Games {
    fun save(game: Game)
}

class InMemoryGames: Games {
    private val games = CopyOnWriteArrayList<Game>()

    override fun save(game: Game) {
        games.add(game)
    }

    fun findAll(): List<Game> {
        return games
    }
}
