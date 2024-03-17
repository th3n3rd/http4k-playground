package com.example.gameplay.infra

import com.example.common.infra.DatabaseContext
import com.example.common.infra.RunDatabaseMigrations
import com.example.common.infra.TestEnvironment
import com.example.common.infra.database.tables.references.GAMES
import com.example.common.infra.database.tables.references.GAME_GUESSES
import com.example.gameplay.Game
import com.example.gameplay.GameId
import com.example.gameplay.Games
import com.example.player.PlayerId
import com.natpryce.hamkrest.greaterThan
import dev.forkhandles.result4k.orThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
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
            won = false,
            attempts = 5,
            guesses = listOf(Game.Guess("first"), Game.Guess("second"))
        )

        games.save(newGame)

        val persistedGame = database
            .select()
            .from(GAMES)
            .where(GAMES.ID.eq(newGame.id.value))
            .and(GAMES.PLAYER_ID.eq(newGame.playerId.value))
            .and(GAMES.SECRET.eq("new"))
            .and(GAMES.WON.eq(false))
            .and(GAMES.ATTEMPTS.eq(5))
            .fetchSingle()

        val persistedGuesses = database
            .select()
            .from(GAME_GUESSES)
            .where(GAME_GUESSES.GAME_ID.eq(newGame.id.value))
            .fetch()

        persistedGame shouldNot beNull()
        persistedGuesses shouldHaveSize 2
    }

    @Test
    fun `find persisted games by id and player`() {
        val existingGameId = GameId()
        val playerId = PlayerId()
        database
            .insertInto(GAMES, GAMES.ID, GAMES.PLAYER_ID, GAMES.SECRET, GAMES.WON)
            .values(existingGameId.value, playerId.value, "existing-one", true)
            .values(GameId().value, playerId.value, "existing-two", false)
            .execute()

        val foundGame = games.findByIdAndPlayerId(existingGameId, playerId)!!
        foundGame shouldBeEqual Game(
            id = existingGameId,
            playerId = playerId,
            secret = "existing-one",
            won = true,
            guesses = listOf()
        )
    }

    @Test
    fun `find games with recorded guesses`() {
        val existingGameId = GameId()
        val playerId = PlayerId()
        database
            .insertInto(GAMES, GAMES.ID, GAMES.PLAYER_ID, GAMES.SECRET, GAMES.WON)
            .values(existingGameId.value, playerId.value, "with-guesses", true)
            .execute()
        database
            .insertInto(GAME_GUESSES, GAME_GUESSES.GAME_ID, GAME_GUESSES.SECRET)
            .values(existingGameId.value, "first")
            .values(existingGameId.value, "second")
            .execute()

        val foundGame = games.findByIdAndPlayerId(existingGameId, playerId)!!
        foundGame shouldBeEqual Game(
            id = existingGameId,
            playerId = playerId,
            secret = "with-guesses",
            won = true,
            guesses = listOf(Game.Guess("first"), Game.Guess("second"))
        )
    }

    @Test
    fun `persist updates for existing games`() {
        val uncompletedGame = Game(
            secret = "updated",
            won = false,
            guesses = listOf(Game.Guess("first"))
        )
        games.save(uncompletedGame)

        val completedGame = uncompletedGame.guess("updated").orThrow()
        games.save(completedGame)

        val persistedGame = database
            .select()
            .from(GAMES)
            .where(GAMES.ID.eq(uncompletedGame.id.value))
            .and(GAMES.PLAYER_ID.eq(uncompletedGame.playerId.value))
            .and(GAMES.SECRET.eq("updated"))
            .and(GAMES.WON.eq(true))
            .fetchSingle()

        val persistedGuesses = database
            .select()
            .from(GAME_GUESSES)
            .where(GAME_GUESSES.GAME_ID.eq(uncompletedGame.id.value))
            .fetch()

        persistedGame shouldNot beNull()
        persistedGuesses shouldHaveSize 2
    }

    @Test
    fun `returns nothing if cannot find a game by id`() {
        database
            .insertInto(GAMES, GAMES.ID, GAMES.PLAYER_ID, GAMES.SECRET, GAMES.WON)
            .values(GameId().value, PlayerId().value, "another", false)
            .execute()

        val foundGame = games.findById(GameId())

        foundGame shouldBe null
    }

    @Test
    fun `returns nothing if cannot find a game by id and player id`() {
        val existingGameId = GameId()
        val anotherPlayerId = PlayerId()
        val currentPlayerId = PlayerId()
        database
            .insertInto(GAMES, GAMES.ID, GAMES.PLAYER_ID, GAMES.SECRET, GAMES.WON)
            .values(existingGameId.value, anotherPlayerId.value, "another-player", true)
            .execute()

        val foundGame = games.findByIdAndPlayerId(existingGameId, currentPlayerId)

        foundGame shouldBe null
    }
}

