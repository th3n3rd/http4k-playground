package com.example.player.infra

import com.example.common.infra.DatabaseContext
import com.example.common.infra.RunDatabaseMigrations
import com.example.common.infra.TestEnvironment
import com.example.common.infra.database.tables.references.PLAYERS
import com.example.player.EncodedPassword
import com.example.player.PlayerId
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import java.util.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseRegisteredPlayersTests {

    private val environment = TestEnvironment()
    private val context = DatabaseContext(environment)
    private val players = RegisteredPlayers.Database(context)

    @BeforeEach
    fun setUp() {
        RunDatabaseMigrations(environment)
    }

    @Test
    fun `persist new registered players`() {
        val newPlayer = RegisteredPlayer(
            username = "new",
            password = EncodedPassword("encoded-new")
        )

        players.save(newPlayer)

        val persistedPlayer = context
            .select()
            .from(PLAYERS)
            .where(PLAYERS.ID.eq(newPlayer.id.value))
            .and(PLAYERS.USERNAME.eq("new"))
            .and(PLAYERS.PASSWORD.eq("encoded-new"))
            .fetchSingle()

        persistedPlayer shouldNot beNull()
    }

    @Test
    fun `find persisted players by username`() {
        val existingPlayerId = PlayerId()
        context
            .insertInto(PLAYERS)
            .values(existingPlayerId.value, "existing-one", "encoded-existing-one")
            .values(UUID.randomUUID(), "existing-two", "encoded-existing-two")
            .execute()

        val foundPlayer = players.findByUsername("existing-one")!!

        foundPlayer shouldBeEqual RegisteredPlayer(
            id = existingPlayerId,
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        )
    }

    @Test
    fun `checks whether a player with a given username exists`() {
        context
            .insertInto(PLAYERS)
            .values(UUID.randomUUID(), "existing-one", "encoded-existing-one")
            .execute()

        players.existByUsername("existing-one") shouldBe true
        players.existByUsername("existing-two") shouldBe false
    }

    @Test
    fun `find persisted players by id`() {
        val existingPlayerId = PlayerId()
        context
            .insertInto(PLAYERS)
            .values(existingPlayerId.value, "existing-one", "encoded-existing-one")
            .values(UUID.randomUUID(), "existing-two", "encoded-existing-two")
            .execute()

        val foundPlayer = players.findById(existingPlayerId)!!

        foundPlayer shouldBeEqual RegisteredPlayer(
            id = existingPlayerId,
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        )
    }

    @Test
    fun `returns nothing if cannot find a player`() {
        players.findByUsername("non-existing") shouldBe null
        players.findById(PlayerId()) shouldBe null
    }
}

