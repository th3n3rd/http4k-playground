package com.example.gameplay.infra

import com.example.gameplay.Game
import com.example.gameplay.Games
import dev.forkhandles.result4k.valueOrNull
import io.kotest.matchers.Matcher
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

interface GamesContract {

    val games: Games

    fun given(game: Game): Game
    fun haveBeenSaved(): Matcher<Game>
    fun haveSavedGuesses(expected: List<String>): Matcher<Game>

    @Test
    fun `persist new games`() {
        val newGame = Game(secret = "new")

        games.save(newGame)

        newGame should haveBeenSaved()
        newGame should haveSavedGuesses(emptyList())
    }

    @Test
    fun `persist games with guesses`() {
        val newGame = Game(
            secret = "new-with-guesses",
            guesses = listOf(
                Game.Guess("first"),
                Game.Guess("second")
            )
        )

        games.save(newGame)

        newGame should haveBeenSaved()
        newGame should haveSavedGuesses(listOf("first", "second"))
    }

    @Test
    fun `find persisted games by id and player`() {
        val first = given(Game(secret = "existing-one"))
        val second = given(Game(secret = "existing-two"))

        val foundFirst = games.findByIdAndPlayerId(first.id, first.playerId)!!
        val foundSecond = games.findByIdAndPlayerId(second.id, second.playerId)!!

        foundFirst shouldBeEqual first
        foundSecond shouldBeEqual second
    }

    @Test
    fun `find games with recorded guesses`() {
        val existingGame = given(
            Game(
                secret = "with-guesses",
                guesses = listOf(Game.Guess("first"), Game.Guess("second"))
            )
        )

        val foundGame = games.findByIdAndPlayerId(existingGame.id, existingGame.playerId)!!

        foundGame shouldBeEqual existingGame
    }

    @Test
    fun `persist updates for existing games`() {
        val existingGame = given(
            Game(
                secret = "with-guesses",
                guesses = listOf(Game.Guess("first"), Game.Guess("second"))
            )
        )

        val updatedGame = existingGame.guess("third").valueOrNull()!!
        games.save(updatedGame)

        existingGame should haveSavedGuesses(listOf("first", "second", "third"))
    }

    @Test
    fun `returns nothing if cannot find a game by id and player id`() {
        val first = given(Game(secret = "another-game"))
        val second = given(Game(secret = "another-player"))

        val foundGame = games.findByIdAndPlayerId(first.id, second.playerId)

        foundGame shouldBe null
    }
}