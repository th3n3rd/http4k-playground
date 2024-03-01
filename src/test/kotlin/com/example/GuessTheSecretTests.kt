package com.example

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GuessTheSecretTests {

    val app = App(InMemoryGames())
    val player = Player(app)

    @Test
    fun `winning gameplay`() {
        val newGame = player.startNewGame()

        assertTrue(player.hasWon(newGame));
    }
}
