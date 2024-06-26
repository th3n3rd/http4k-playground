package com.example.gameplay.infra

import com.example.gameplay.Game
import com.example.gameplay.Games
import com.example.gameplay.GamesContract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

class InMemoryGamesTests : GamesContract {

    override val games: Games = Games.InMemory()

    override fun given(game: Game): Game {
        games.save(game)
        return game
    }

    override fun hasBeenStored() = Matcher<Game> { game ->
        MatcherResult(
            games.findByIdAndPlayerId(game.id, game.playerId) != null,
            { "$game was not saved" },
            { "$game should not have been saved" },
        )
    }

    override fun hasStoredGuesses(expected: List<String>) = Matcher<Game> { game ->
        MatcherResult(
            games.findByIdAndPlayerId(game.id, game.playerId)
                ?.guesses
                ?.map { it.secret }
                ?.sorted() == expected.sorted(),
            { "$game was not saved" },
            { "$game should not have been saved" },
        )
    }
}