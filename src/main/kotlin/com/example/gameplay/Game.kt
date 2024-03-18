package com.example.gameplay

import com.example.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

data class Game(
    val id: GameId = GameId(),
    val playerId: PlayerId = PlayerId(),
    val secret: String = "",
    val won: Boolean = false,
    val guesses: List<Guess>? = null
) {
    val attempts = guesses.orEmpty().size
    val hint = HintProgression(secret, attempts)

    fun guess(secret: String): Result<Game, Exception> {
        if (won) {
            return Failure(GameAlreadyCompleted(id))
        }
        return Success(copy(
            won = this.secret == secret,
            guesses = guesses.orEmpty() + Guess(secret)
        ))
    }

    fun ownedBy(playerId: PlayerId) = this.playerId == playerId

    data class Guess(val secret: String)
}
