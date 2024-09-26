package com.example.guessing.player.infra

import com.example.guessing.common.infra.RepositoryCall
import com.example.guessing.common.infra.RepositoryTracing
import com.example.guessing.player.EncodedPassword
import com.example.guessing.player.RegisteredPlayer
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.RegisteredPlayersContract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test


class RepositoryTracingPlayersTests : RegisteredPlayersContract {

    private val events = RecordingEvents()
    private val inMemoryPlayers = RegisteredPlayers.InMemory();

    override val players = RepositoryTracing<RegisteredPlayers>(
        inMemoryPlayers,
        events,
    )

    @Test
    fun `publish a new event when saving a player`() {
        val newPlayer = RegisteredPlayer(
            username = "new-player",
            password = EncodedPassword("dont-care")
        )

        players.save(newPlayer)

        events.toList() shouldBe listOf(RepositoryCall("RegisteredPlayers", "save"))
    }

    @Test
    fun `publish a new event when retrieving an existing player`() {
        val existingPlayer = given(
            RegisteredPlayer(
                username = "existing-player",
                password = EncodedPassword("dont-care")
            )
        )

        players.findByUsername(existingPlayer.username)

        events.toList() shouldBe listOf(RepositoryCall("RegisteredPlayers", "findByUsername"))
    }

    @Test
    fun `publish a new event when checking if a player already exist`() {
        val existingPlayer = given(
            RegisteredPlayer(
                username = "existing-player",
                password = EncodedPassword("dont-care")
            )
        )

        players.existByUsername(existingPlayer.username)
        players.existByUsername("non-existing")

        events.toList() shouldBe listOf(
            RepositoryCall("RegisteredPlayers", "existByUsername"),
            RepositoryCall("RegisteredPlayers", "existByUsername"),
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

        events.toList() shouldBe listOf(RepositoryCall("RegisteredPlayers", "findById"))
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
