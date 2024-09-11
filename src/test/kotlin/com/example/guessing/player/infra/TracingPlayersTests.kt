package com.example.guessing.player.infra

import com.example.guessing.common.infra.DatabaseCall
import com.example.guessing.player.EncodedPassword
import com.example.guessing.player.RegisteredPlayer
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.RegisteredPlayersContract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test


class TracingPlayersTests : RegisteredPlayersContract {

    private val events = RecordingEvents()
    private val inMemoryPlayers = RegisteredPlayers.InMemory();

    override val players = TracingRegisteredPlayers(
        events,
        inMemoryPlayers
    )

    @Test
    fun `publish a new event when saving a player`() {
        val newPlayer = RegisteredPlayer(
            username = "new-player",
            password = EncodedPassword("dont-care")
        )

        players.save(newPlayer)

        events.toList() shouldBe listOf(DatabaseCall("players", "save"))
    }

    @Test
    fun  `publish a new event when retrieving an existing player`() {
        val existingPlayer = given(
            RegisteredPlayer(
            username = "existing-player",
            password = EncodedPassword("dont-care")
        )
        )

        players.findByUsername(existingPlayer.username)

        events.toList() shouldBe listOf(DatabaseCall("players", "find by username"))
    }

    @Test
    fun  `publish a new event when checking if a player already exist`() {
        val existingPlayer = given(
            RegisteredPlayer(
            username = "existing-player",
            password = EncodedPassword("dont-care")
        )
        )

        players.existByUsername(existingPlayer.username)
        players.existByUsername("non-existing")

        events.toList() shouldBe listOf(
            DatabaseCall("players", "exist by username"),
            DatabaseCall("players", "exist by username"),
        )
    }

    @Test
    fun `publish a new event when retrieving an existing player by id`() {
        val existingPlayer = given(
            RegisteredPlayer(
            username = "existing-player",
            password = EncodedPassword("dont-care")
        )
        )

        players.findById(existingPlayer.id)

        events.toList() shouldBe listOf(DatabaseCall("players", "find by id"))
    }

    override fun given(player: RegisteredPlayer): RegisteredPlayer {
        inMemoryPlayers.save(player)
        return player
    }

    override fun haveBeenStored() = Matcher<RegisteredPlayer> { player ->
        MatcherResult(
            inMemoryPlayers.existByUsername(player.username),
            { "$player was not saved" },
            { "$player should not have been saved" },
        )
    }
}
