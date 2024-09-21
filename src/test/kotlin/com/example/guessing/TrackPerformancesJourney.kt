package com.example.guessing

import org.junit.jupiter.api.Test

interface TrackPerformancesJourney : Journey {

    @Test
    fun `track performance`() {
        val alice = newPlayer("alice")
            .startsNewGame()
            .makesGuess("incorrect")
            .makesGuess("incorrect")
            .makesGuess("incorrect")
            .makesGuess("correct")

        val bob = newPlayer("bob")
            .startsNewGame()
            .makesGuess("correct")

        val charlie = newPlayer("charlie")
            .startsNewGame()
            .makesGuess("incorrect")
            .makesGuess("correct")

        bob.confirmsLeaderboardContains(
            mapOf(
                bob.username to 100,
                charlie.username to 50,
                alice.username to 25
            )
        )
    }
}