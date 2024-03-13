package com.example.gameplay

import com.example.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

data class Game(
    val id: GameId = GameId(),
    val playerId: PlayerId = PlayerId(),
    val secret: String = "",
    val attempts: Int = 0,
    val won: Boolean = false
) {

    val hint = HintProgression(secret, attempts)

    fun guess(secret: String): Result<Game, Exception> {
        if (won) {
            return Failure(GameAlreadyCompleted(id))
        }
        return Success(copy(
            attempts = attempts + 1,
            won = this.secret == secret
        ))
    }

    fun ownedBy(playerId: PlayerId) = this.playerId == playerId
}
