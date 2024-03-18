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
import dev.forkhandles.result4k.orThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

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
            guesses = listOf(
                Game.Guess("first"),
                Game.Guess("second")
            )
        )

        games.save(newGame)

        val persistedGame = lookupPersistedGame(
            gameId = newGame.id.value,
            playerId = newGame.playerId.value,
            secret = "new"
        )
        val persistedGuesses = lookupPersistedGuesses(newGame.id)

        persistedGame shouldNotBe null
        persistedGuesses shouldHaveSize 2
    }

    @Test
    fun `find persisted games by id and player`() {
        val existingGameId = GameId()
        val playerId = PlayerId()
        persistGame(gameId = existingGameId, playerId = playerId, secret = "existing-one")
        persistGame(gameId = GameId(), playerId = playerId, secret = "existing-two")

        val foundGame = games.findByIdAndPlayerId(existingGameId, playerId)!!

        foundGame shouldBeEqual Game(
            id = existingGameId,
            playerId = playerId,
            secret = "existing-one",
            guesses = listOf()
        )
    }

    @Test
    fun `find games with recorded guesses`() {
        val existingGameId = GameId()
        val playerId = PlayerId()
        persistGame(
            gameId = existingGameId,
            playerId = playerId,
            secret = "with-guesses"
        )
        persistGuesses(existingGameId, listOf("first", "second"))

        val foundGame = games.findByIdAndPlayerId(existingGameId, playerId)!!
        foundGame shouldBeEqual Game(
            id = existingGameId,
            playerId = playerId,
            secret = "with-guesses",
            guesses = listOf(Game.Guess("first"), Game.Guess("second"))
        )
    }

    private fun persistGuesses(existingGameId: GameId, guesses: List<String>) {
        guesses.forEach {
            database
                .insertInto(GAME_GUESSES, GAME_GUESSES.GAME_ID, GAME_GUESSES.SECRET)
                .values(existingGameId.value, it)
                .execute()
        }
    }

    @Test
    fun `persist updates for existing games`() {
        val uncompletedGame = Game(
            secret = "updated",
            guesses = listOf(Game.Guess("first"))
        )
        games.save(uncompletedGame)

        val completedGame = uncompletedGame.guess("updated").orThrow()
        games.save(completedGame)

        val persistedGame = lookupPersistedGame(
            uncompletedGame.id.value,
            uncompletedGame.playerId.value,
            "updated"
        )
        val persistedGuesses = lookupPersistedGuesses(uncompletedGame.id)

        persistedGame shouldNot beNull()
        persistedGuesses shouldHaveSize 2
    }

    @Test
    fun `returns nothing if cannot find a game by id`() {
        persistGame(
            gameId = GameId(),
            playerId = PlayerId(),
            secret = "another"
        )

        val foundGame = games.findById(GameId())

        foundGame shouldBe null
    }

    @Test
    fun `returns nothing if cannot find a game by id and player id`() {
        val existingGameId = GameId()
        val anotherPlayerId = PlayerId()
        val currentPlayerId = PlayerId()
        persistGame(
            gameId = existingGameId,
            playerId = anotherPlayerId,
            secret = "another-player"
        )

        val foundGame = games.findByIdAndPlayerId(existingGameId, currentPlayerId)

        foundGame shouldBe null
    }

    private fun persistGame(gameId: GameId, playerId: PlayerId, secret: String) {
        database
            .insertInto(GAMES, GAMES.ID, GAMES.PLAYER_ID, GAMES.SECRET)
            .values(gameId.value, playerId.value, secret)
            .execute()
    }

    private fun lookupPersistedGuesses(gameId: GameId) = database
        .select()
        .from(GAME_GUESSES)
        .where(GAME_GUESSES.GAME_ID.eq(gameId.value))
        .fetch()

    private fun lookupPersistedGame(gameId: UUID, playerId: UUID, secret: String) = database
        .select()
        .from(GAMES)
        .where(GAMES.ID.eq(gameId))
        .and(GAMES.PLAYER_ID.eq(playerId))
        .and(GAMES.SECRET.eq(secret))
        .fetchSingle()
}

