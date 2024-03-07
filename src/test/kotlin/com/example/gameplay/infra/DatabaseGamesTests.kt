package com.example.gameplay.infra

import com.example.common.infra.DatabaseContext
import com.example.common.infra.RunDatabaseMigrations
import com.example.common.infra.TestEnvironment
import com.example.common.infra.database.tables.references.GAMES
import com.example.gameplay.Game
import com.example.gameplay.GameId
import com.example.gameplay.Games
import dev.forkhandles.result4k.orThrow
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseGamesTests {

    private val environment = TestEnvironment()
    private val database = DatabaseContext(environment)
    private val games = Games.Database(database)

    @BeforeEach
    fun setUp() {
        RunDatabaseMigrations(environment)
    }

    @Test
    fun `persist new games`() {
        val newGame = Game(
            secret = "new",
            won = false
        )

        games.save(newGame)

        val persistedGame = database
            .select()
            .from(GAMES)
            .where(GAMES.ID.eq(newGame.id.value))
            .and(GAMES.SECRET.eq("new"))
            .and(GAMES.WON.eq(false))
            .fetchSingle()

        persistedGame shouldNot beNull()
    }

    @Test
    fun `find persisted games by id`() {
        val existingGameId = GameId()
        database
            .insertInto(GAMES)
            .values(existingGameId.value, "existing-one", true)
            .values(GameId().value, "existing-two", false)
            .execute()

        val foundGame = games.findById(existingGameId)!!
        foundGame shouldBeEqual Game(
            id = existingGameId,
            secret = "existing-one",
            won = true
        )
    }

    @Test
    fun `persist updates for existing games`() {
        val uncompletedGame = Game(
            secret = "updated",
            won = false
        )
        games.save(uncompletedGame)

        val completedGame = uncompletedGame.guess("updated").orThrow()
        games.save(completedGame)

        val persistedGame = database
            .select()
            .from(GAMES)
            .where(GAMES.ID.eq(uncompletedGame.id.value))
            .and(GAMES.SECRET.eq("updated"))
            .and(GAMES.WON.eq(true))
            .fetchSingle()

        persistedGame shouldNot beNull()
    }

    @Test
    fun `returns nothing if cannot find a game by id`() {
        val foundGame = games.findById(GameId())

        foundGame shouldBe null
    }

}

