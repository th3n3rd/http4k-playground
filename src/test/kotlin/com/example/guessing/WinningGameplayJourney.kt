package com.example.guessing

import org.junit.jupiter.api.Test

interface WinningGameplayJourney : Journey {
    @Test
    fun `winning gameplay`() {
        newPlayer("alice")
            .startsNewGame()
            .confirmsReceivedHint("_______")

            .makesGuess("incorrect")
            .confirmsReceivedHint("c______")

            .makesGuess("incorrect")
            .confirmsReceivedHint("c_____t")

            .makesGuess("correct")
            .confirmsHasWon()
    }
}