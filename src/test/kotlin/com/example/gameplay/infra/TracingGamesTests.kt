package com.example.gameplay.infra

import com.example.common.infra.DatabaseCall
import com.example.gameplay.Game
import com.example.gameplay.Games
import com.example.player.PlayerId
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test


class TracingGamesTests : GamesContract {

    private val events = RecordingEvents()
    private val inMemoryGames = Games.InMemory()
    override val games: Games = TracingGames(
        events,
        inMemoryGames
    )

    @Test
    fun `publish a new event when saving a game`() {
        val newGame = Game(playerId = PlayerId(), secret = "new-game")

        games.save(newGame)

        events.toList() shouldBe listOf(DatabaseCall("games", "save"))
    }

    @Test
    fun `publish a new event when retrieving an existing game`() {
        val existingGame = given(Game(playerId = PlayerId(), secret = "existing-game"))

        games.findByIdAndPlayerId(existingGame.id, existingGame.playerId)

        events.toList() shouldBe listOf(DatabaseCall("games", "find by id and player id"))
    }

    override fun given(game: Game): Game {
        inMemoryGames.save(game)
        return game
    }

    override fun haveBeenSaved() = Matcher<Game> { game ->
        MatcherResult(
            inMemoryGames.findByIdAndPlayerId(game.id, game.playerId) != null,
            { "$game was not saved" },
            { "$game should not have been saved" },
        )
    }

    override fun haveSavedGuesses(expected: List<String>) = Matcher<Game> { game ->
        MatcherResult(
            inMemoryGames.findByIdAndPlayerId(game.id, game.playerId)
                ?.guesses
                ?.map { it.secret }
                ?.sorted() == expected.sorted(),
            { "$game was not saved" },
            { "$game should not have been saved" },
        )
    }
}