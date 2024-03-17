package com.example.gameplay

import com.example.gameplay.infra.InMemory
import com.example.player.PlayerId
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SubmitGuessTests {

    private val games = Games.InMemory()
    private val submitGuess = SubmitGuess(games)

    @Test
    fun `marks the game as won when the guess is correct`() {
        val game = Game(secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "correct", game.playerId)

        result shouldBeSuccess {
            it.won shouldBe true
        }
    }

    @Test
    fun `each guess increases the recorded number of attempts`() {
        val game = Game(attempts = 3, secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "correct", game.playerId)

        result shouldBeSuccess {
            it.attempts shouldBe 4
        }
    }

    @Test
    fun `each guess is recorded`() {
        val game = Game(secret = "correct", guesses = listOf())
        games.save(game)

        val result = submitGuess(game.id, "first", game.playerId)
            .flatMap { submitGuess(game.id, "second", game.playerId) }
            .flatMap { submitGuess(game.id, "third", game.playerId) }

        result.shouldBeSuccess {
            it.guesses!! shouldBeEqual listOf(
                Game.Guess("first"),
                Game.Guess("second"),
                Game.Guess("third"),
            )
        }
    }

    @Test
    fun `leaves the game as NOT won when the guess is incorrect`() {
        val game = Game(secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "incorrect", game.playerId)

        result shouldBeSuccess {
            it.won shouldBe false
        }
    }

    @Test
    fun `persist the updated game`() {
        val game = Game(secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "correct", game.playerId)

        result shouldBeSuccess {
            games.findById(it.id) shouldBe it
        }
    }

    @Test
    fun `fails when the game has already been marked as won`() {
        val game = Game(secret = "correct", won = true)
        games.save(game)

        val result = submitGuess(game.id, "correct", game.playerId)

        result shouldBeFailure GameAlreadyCompleted(game.id)
    }

    @Test
    fun `fails when the game is not found`() {
        val nonExistentGameId = GameId()
        val result = submitGuess(nonExistentGameId, "correct", PlayerId())

        result shouldBeFailure GameNotFound(nonExistentGameId)
    }

    @Test
    fun `fails when the game is not found for the given player`() {
        val currentPlayerId = PlayerId()
        val game = Game(playerId = PlayerId(), secret = "correct", won = false)
        games.save(game)

        val result = submitGuess(game.id, "correct", currentPlayerId)

        result shouldBeFailure GameNotFound(game.id)
    }
}
