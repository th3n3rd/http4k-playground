package com.example

import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.should
import java.util.concurrent.CopyOnWriteArrayList
import org.junit.jupiter.api.Test

class StartNewGameTests {

    private val games = InMemoryGames()

    @Test
    fun `starts a different game every time`() {
        val first = StartNewGame(games)
        val second = StartNewGame(games)

        first shouldNotBeEqual second
    }

    @Test
    fun `new games get persisted`() {
        val first = StartNewGame(games)
        val second = StartNewGame(games)

        games.findAll() should containOnly(first, second)
    }
}

data class Game(val id: GameId = GameId())

interface Games {
    fun save(game: Game)
}

fun StartNewGame(games: Games): Game {
    val newGame = Game()
    games.save(newGame)
    return newGame
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
