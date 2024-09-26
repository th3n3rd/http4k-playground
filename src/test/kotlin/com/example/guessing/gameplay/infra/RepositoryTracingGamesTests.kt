package com.example.guessing.gameplay.infra

import com.example.guessing.common.infra.RepositoryCall
import com.example.guessing.common.infra.RepositoryTracing
import com.example.guessing.gameplay.Game
import com.example.guessing.gameplay.Games
import com.example.guessing.gameplay.GamesContract
import com.example.guessing.player.PlayerId
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test

class RepositoryTracingGamesTests : GamesContract {

    private val events = RecordingEvents()
    private val inMemoryGames = Games.InMemory()
    override val games: Games = RepositoryTracing<Games>(inMemoryGames, events)

    @Test
    fun `publish a new event when saving a game`() {
        val newGame = Game(playerId = PlayerId(), secret = "new-game")

        games.save(newGame)

        events.toList() shouldBe listOf(RepositoryCall("Games", "save"))
    }

    @Test
    fun `publish a new event when retrieving an existing game`() {
        val existingGame = given(Game(playerId = PlayerId(), secret = "existing-game"))

        games.findByIdAndPlayerId(existingGame.id, existingGame.playerId)

        events.toList() shouldBe listOf(RepositoryCall("Games", "findByIdAndPlayerId"))
    }

    override fun given(game: Game): Game {
        inMemoryGames.save(game)
        return game
    }

    override fun hasBeenStored() = Matcher<Game> { game ->
        MatcherResult(
            inMemoryGames.findByIdAndPlayerId(game.id, game.playerId) != null,
            { "$game was not saved" },
            { "$game should not have been saved" },
        )
    }

    override fun hasStoredGuesses(expected: List<String>) = Matcher<Game> { game ->
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