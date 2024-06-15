package com.example.gameplay

import com.example.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

data class Game(
    val id: GameId = GameId(),
    val playerId: PlayerId = PlayerId(),
    val secret: String = "",
    val guesses: List<Guess> = listOf()
) {
    val attempts = guesses.size
    val hint = HintProgression(secret, attempts)
    val won = guesses.any { it.secret == secret }

    fun guess(secret: String): Result<Game, Exception> {
        if (won) {
            return Failure(GameGuessingError.GameAlreadyCompleted(id))
        }
        return Success(copy(
            guesses = guesses + Guess(secret)
        ))
    }

    fun ownedBy(playerId: PlayerId) = this.playerId == playerId

    data class Guess(val secret: String)
}

sealed interface GameGuessingError {
    data class GameAlreadyCompleted(val gameId: GameId) : RuntimeException("Game with id ${gameId.value} is already completed"), GameGuessingError
}