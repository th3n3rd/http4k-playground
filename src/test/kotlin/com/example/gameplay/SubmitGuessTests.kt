package com.example.gameplay

import com.example.gameplay.GameGuessingError.GameAlreadyCompleted
import com.example.gameplay.SubmitGuessError.CouldNotGuess
import com.example.gameplay.infra.InMemory
import com.example.player.PlayerId
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test

class SubmitGuessTests {

    private val events = RecordingEvents()
    private val games = Games.InMemory()
    private val submitGuess = SubmitGuess(games, events)

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
        val game = Game(secret = "correct", guesses = listOf(
            Game.Guess("first"),
            Game.Guess("second"),
            Game.Guess("third")
        ))
        games.save(game)

        val result = submitGuess(game.id, "correct", game.playerId)

        result shouldBeSuccess {
            it.attempts shouldBe 4
        }
    }

    @Test
    fun `each guess is recorded`() {
        val game = Game(secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "first", game.playerId)
            .flatMap { submitGuess(game.id, "second", game.playerId) }
            .flatMap { submitGuess(game.id, "third", game.playerId) }

        result.shouldBeSuccess {
            it.guesses shouldBeEqual listOf(
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
            games.findByIdAndPlayerId(it.id, it.playerId) shouldBe it
        }
    }

    @Test
    fun `publish an event when a game is completed`() {
        val first = Game(secret = "correct")
        val second = Game(secret = "correct")
        val third = Game(secret = "correct")
        games.save(first)
        games.save(second)
        games.save(third)

        submitGuess(first.id, "correct", first.playerId)
        submitGuess(second.id, "incorrect", second.playerId)
        submitGuess(third.id, "incorrect", third.playerId)
        submitGuess(third.id, "correct", third.playerId)

        events.toList() shouldBe listOf(
            GameCompleted(gameId = first.id, playerId = first.playerId, attempts = 1),
            GameCompleted(gameId = third.id, playerId = third.playerId, attempts = 2),
        )
    }

    @Test
    fun `fails when the game has already been marked as won`() {
        val game = Game(secret = "correct", guesses = listOf(Game.Guess("correct")))
        games.save(game)

        val result = submitGuess(game.id, "correct", game.playerId)

        result shouldBeFailure CouldNotGuess(reason = GameAlreadyCompleted(game.id))
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
        val game = Game(playerId = PlayerId(), secret = "correct")
        games.save(game)

        val result = submitGuess(game.id, "correct", currentPlayerId)

        result shouldBeFailure GameNotFound(game.id)
    }
}
