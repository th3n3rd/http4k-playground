package com.example.gameplay

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SubmitGuessTests {

    private val games = Games.InMemory()
    private val submitGuess = SubmitGuess(games)

    @Test
    fun `marks the game as won then guess is correct`() {
        val game = Game(secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "correct")

        result shouldBeSuccess {
            it.won shouldBe true
        }
    }

    @Test
    fun `marks the game has NOT won when the guess is incorrect`() {
        val game = Game(secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "incorrect")

        result shouldBeSuccess {
            it.won shouldBe false
        }
    }

    @Test
    fun `persist the updated game`() {
        val game = Game(secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "correct")

        result shouldBeSuccess {
            games.findById(it.id) shouldBe it
        }
    }

    @Test
    fun `fails when the game has already been marked as won`() {
        val game = Game(secret = "correct", won = true)
        games.save(game)

        val result = submitGuess(game.id, "correct")

        result shouldBeFailure GameAlreadyCompleted(game.id)
    }

    @Test
    fun `fails when the game is not found`() {
        val nonExistentGameId = GameId()
        val result = submitGuess(nonExistentGameId, "correct")

        result shouldBeFailure GameNotFound(nonExistentGameId)
    }
}
