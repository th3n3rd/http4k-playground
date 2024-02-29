package com.example

import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.should
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
