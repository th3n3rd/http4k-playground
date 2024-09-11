package com.example.guessing.gameplay.infra

import com.example.guessing.common.infra.DatabaseContext
import com.example.guessing.common.infra.RunDatabaseMigrations
import com.example.guessing.common.infra.TestEnvironment
import com.example.guessing.common.infra.database.tables.references.GAMES
import com.example.guessing.common.infra.database.tables.references.GAME_GUESSES
import com.example.guessing.gameplay.Game
import com.example.guessing.gameplay.Games
import com.example.guessing.gameplay.GamesContract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import org.junit.jupiter.api.BeforeEach

class DatabaseGamesTests : GamesContract {

    private val environment = TestEnvironment()
    private val database = DatabaseContext(environment)

    override val games = Games.Database(database)

    @BeforeEach
    fun setUp() {
        RunDatabaseMigrations(environment)
    }

    override fun given(game: Game): Game {
        database
            .insertInto(GAMES, GAMES.ID, GAMES.PLAYER_ID, GAMES.SECRET)
            .values(game.id.value, game.playerId.value, game.secret)
            .execute()

        game.guesses.forEach {
            database
                .insertInto(GAME_GUESSES, GAME_GUESSES.GAME_ID, GAME_GUESSES.SECRET)
                .values(game.id.value, it.secret)
                .execute()
        }

        return game
    }

    override fun hasBeenStored() = Matcher<Game> { game ->
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

    override fun hasStoredGuesses(expected: List<String>) = Matcher<Game> { game ->
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

