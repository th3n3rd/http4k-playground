package com.example.gameplay

import com.example.gameplay.SubmitGuessError.CouldNotGuess
import com.example.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.peek
import org.http4k.events.Events

class SubmitGuess(private val games: Games, private val events: Events = {}) {
    operator fun invoke(gameId: GameId, secret: String, playerId: PlayerId): Result<Game, Exception> {
        val game = games.findByIdAndPlayerId(gameId, playerId) ?: return Failure(GameNotFound(gameId))
        return game.guess(secret)
            .mapFailure { CouldNotGuess(it) }
            .peek {
                games.save(it)
                if (it.won) {
                    events(GameCompleted(
                        gameId = it.id,
                        playerId = it.playerId,
                        attempts = it.attempts
                    ))
            }
        }
    }
}

sealed interface SubmitGuessError {
    data class CouldNotGuess(val reason: GameGuessingError) : SubmitGuessError, Exception()
}