package com.example.guessing.gameplay

import com.example.architecture.Architecture
import com.example.guessing.gameplay.SubmitGuessError.CouldNotGuess
import com.example.guessing.gameplay.SubmitGuessError.GameNotFound
import com.example.guessing.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.peek
import org.http4k.events.Events

@Architecture.UseCase
class SubmitGuess(
    private val games: Games,
    private val events: Events = {}
) {
    operator fun invoke(gameId: GameId, secret: String, playerId: PlayerId): Result<Game, SubmitGuessError> {
        val game = games.findByIdAndPlayerId(gameId, playerId) ?: return Failure(GameNotFound(gameId))
        return game.guess(secret)
            .mapFailure { CouldNotGuess(it) }
            .peek {
                games.save(it)
                if (it.won) {
                    events(
                        GameCompleted(
                        gameId = it.id,
                        playerId = it.playerId,
                        attempts = it.attempts
                    )
                    )
            }
        }
    }
}

sealed interface SubmitGuessError {
    data class GameNotFound(val gameId: GameId) : SubmitGuessError, RuntimeException("Game with id ${gameId.value} not found")
    data class CouldNotGuess(val reason: GameGuessingError) : SubmitGuessError, Exception()
}