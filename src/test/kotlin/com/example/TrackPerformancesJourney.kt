package com.example

import org.junit.jupiter.api.Test

interface TrackPerformancesJourney : Journey {

    @Test
    fun `track performance`() {
        val alice = newPlayer("alice").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "incorrect")
            it.guess(newGame, "incorrect")
            it.guess(newGame, "incorrect")
            it.guess(newGame, "correct")
        }

        val bob = newPlayer("bob").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "correct")
        }

        val charlie = newPlayer("charlie").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "incorrect")
            it.guess(newGame, "correct")
        }

        bob.checkLeaderboard(
            mapOf(
                bob.username to 100,
                charlie.username to 50,
                alice.username to 25
            )
        )
    }
}