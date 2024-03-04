package com.example.gameplay

import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.should
import org.junit.jupiter.api.Test

class StartNewGameTests {

    private val secrets = RotatingSecrets(listOf("first", "second"))
    private val games = InMemoryGames()
    private val startNewGame = StartNewGame(games, secrets)

    @Test
    fun `starts a different game every time`() {
        val first = startNewGame()
        val second = startNewGame()

        first shouldNotBeEqual second
    }

    @Test
    fun `new games get a random secret to guess`() {
        val first = startNewGame()
        val second = startNewGame()

        first.secret shouldNotBeEqual second.secret
    }

    @Test
    fun `new games get persisted`() {
        val first = startNewGame()
        val second = startNewGame()

        games.findAll() should containOnly(first, second)
    }
}
