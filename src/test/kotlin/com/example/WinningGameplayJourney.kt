package com.example

import org.junit.jupiter.api.Test

interface WinningGameplayJourney : Journey {
    @Test
    fun `winning gameplay`() {
        val alice = newPlayer("alice")
        val newGame = alice.startNewGame()

        alice.receivedHint(newGame, "_______")

        alice.guess(newGame, "incorrect")
        alice.receivedHint(newGame, "c______")

        alice.guess(newGame, "incorrect")
        alice.receivedHint(newGame, "c_____t")

        alice.guess(newGame, "correct")

        alice.hasWon(newGame)
    }
}