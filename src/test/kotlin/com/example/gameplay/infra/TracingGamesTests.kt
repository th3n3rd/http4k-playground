package com.example.gameplay.infra

import com.example.common.infra.DatabaseCall
import com.example.gameplay.Game
import com.example.gameplay.Games
import com.example.player.PlayerId
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test


class TracingGamesTests {

    private val events = RecordingEvents()
    private val games = Games.InMemory();
    private val tracingGames = TracingGames(
        events,
        games
    )

    @Test
    fun `publish a new event when saving a game`() {
        val newGame = Game(playerId = PlayerId(), secret = "new-game")

        tracingGames.save(newGame)

        games.findByIdAndPlayerId(newGame.id, newGame.playerId) shouldNot beNull()
        events.toList() shouldBe listOf(DatabaseCall("games", "save"))
    }

    @Test
    fun  `publish a new event when retrieving an existing game`() {
        val existingGame = Game(playerId = PlayerId(), secret = "existing-game")
        games.save(existingGame)

        val retrievedGame = tracingGames.findByIdAndPlayerId(existingGame.id, existingGame.playerId)

        retrievedGame shouldBe existingGame
        events.toList() shouldBe listOf(DatabaseCall("games", "find by id and player id"))
    }
}