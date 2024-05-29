package com.example.player.infra

import com.example.common.infra.DatabaseCall
import com.example.player.EncodedPassword
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test


class TracingPlayersTests {

    private val events = RecordingEvents()
    private val players = RegisteredPlayers.InMemory();
    private val tracingPlayers = TracingRegisteredPlayers(
        events,
        players
    )

    @Test
    fun `publish a new event when saving a player`() {
        val newPlayer = RegisteredPlayer(
            username = "new-player",
            password = EncodedPassword("dont-care")
        )

        tracingPlayers.save(newPlayer)

        players.findByUsername("new-player") shouldNotBe null
        events.toList() shouldBe listOf(DatabaseCall("players", "save"))
    }

    @Test
    fun  `publish a new event when retrieving an existing player`() {
        val existingPlayer = RegisteredPlayer(
            username = "existing-player",
            password = EncodedPassword("dont-care")
        )
        players.save(existingPlayer)

        val retrievedPlayer = tracingPlayers.findByUsername(existingPlayer.username)

        retrievedPlayer shouldBe existingPlayer
        events.toList() shouldBe listOf(DatabaseCall("players", "find by username"))
    }

    @Test
    fun  `publish a new event when checking if a player already exist`() {
        val existingPlayer = RegisteredPlayer(
            username = "existing-player",
            password = EncodedPassword("dont-care")
        )
        players.save(existingPlayer)

        tracingPlayers.existByUsername(existingPlayer.username) shouldBe true
        tracingPlayers.existByUsername("non-existing") shouldBe false
        events.toList() shouldBe listOf(
            DatabaseCall("players", "exist by username"),
            DatabaseCall("players", "exist by username"),
        )
    }
}