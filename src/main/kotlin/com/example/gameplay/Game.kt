package com.example.gameplay

import com.example.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

data class Game(
    val id: GameId = GameId(),
    val playerId: PlayerId = PlayerId(),
    val secret: String = "",
    val won: Boolean = false
) {

    val hint = "_".repeat(secret.length)

    fun guess(playerId: PlayerId, secret: String): Result<Game, Exception> {
        if (won) {
            return Failure(GameAlreadyCompleted(id))
        }
        if (!ownedBy(playerId)) {
            return Failure(GameOwnershipMismatch(id))
        }
        return Success(copy(won = this.secret == secret))
    }

    fun ownedBy(playerId: PlayerId) = this.playerId == playerId
}
