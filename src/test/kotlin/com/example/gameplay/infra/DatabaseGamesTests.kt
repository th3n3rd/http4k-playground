package com.example.gameplay.infra

import com.example.common.infra.DatabaseContext
import com.example.common.infra.RunDatabaseMigrations
import com.example.common.infra.TestEnvironment
import com.example.common.infra.database.tables.references.GAMES
import com.example.common.infra.database.tables.references.GAME_GUESSES
import com.example.gameplay.Game
import com.example.gameplay.Games
import dev.forkhandles.result4k.orThrow
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
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
        val newGame = Game(secret = "new")

        games.save(newGame)

        newGame should bePersisted()
        newGame should havePersistedGuesses(emptyList())
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

        newGame should bePersisted()
        newGame should havePersistedGuesses(listOf("first", "second"))
    }

    @Test
    fun `find persisted games by id and player`() {
        val first = persist(Game(secret = "existing-one"))
        val second = persist(Game(secret = "existing-two"))

        val foundFirst = games.findByIdAndPlayerId(first.id, first.playerId)!!
        val foundSecond = games.findByIdAndPlayerId(second.id, second.playerId)!!

        foundFirst shouldBeEqual first
        foundSecond shouldBeEqual second
    }

    @Test
    fun `find games with recorded guesses`() {
        val existingGame = persist(Game(
            secret = "with-guesses",
            guesses = listOf(Game.Guess("first"), Game.Guess("second"))
        ))

        val foundGame = games.findByIdAndPlayerId(existingGame.id, existingGame.playerId)!!

        foundGame shouldBeEqual existingGame
    }

    @Test
    fun `persist updates for existing games`() {
        val existingGame = persist(Game(
            secret = "with-guesses",
            guesses = listOf(Game.Guess("first"), Game.Guess("second"))
        ))

        val updatedGame = existingGame.guess("third").orThrow()
        games.save(updatedGame)

        existingGame should havePersistedGuesses(listOf("first", "second", "third"))
    }

    @Test
    fun `returns nothing if cannot find a game by id and player id`() {
        val first = persist(Game(secret = "another-game"))
        val second = persist(Game(secret = "another-player"))

        val foundGame = games.findByIdAndPlayerId(first.id, second.playerId)

        foundGame shouldBe null
    }

    private fun persist(game: Game): Game {
        database
            .insertInto(GAMES, GAMES.ID, GAMES.PLAYER_ID, GAMES.SECRET)
            .values(game.id.value, game.playerId.value, game.secret)
            .execute()

        game.guesses?.forEach {
            database
                .insertInto(GAME_GUESSES, GAME_GUESSES.GAME_ID, GAME_GUESSES.SECRET)
                .values(game.id.value, it.secret)
                .execute()
        }

        return game
    }

    private fun bePersisted() = Matcher<Game> { game ->
        MatcherResult(
            database
                .fetchExists(
                    database.select()
                        .from(GAMES)
                        .where(GAMES.ID.eq(game.id.value))
                        .and(GAMES.PLAYER_ID.eq(game.playerId.value))
                        .and(GAMES.SECRET.eq(game.secret))
                ),
            { "$game was not persisted" },
            { "$game should not have been persisted" },
        )
    }

    private fun havePersistedGuesses(expected: List<String>) = Matcher<Game> { game ->
        val actual = database.select(GAME_GUESSES.SECRET)
            .from(GAME_GUESSES)
            .where(GAME_GUESSES.GAME_ID.eq(game.id.value))
            .fetchInto(String::class.java)

        MatcherResult(
            actual.sorted() == expected.sorted(),
            { "Expected persisted guesses $actual to contain $expected" },
            { "Expected persisted guesses $actual not to contain $expected" },
        )
    }
}

