package com.example.gameplay

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

data class Game(val id: GameId = GameId(), val secret: String = "", val won: Boolean = false) {

    val hint = "_".repeat(secret.length)

    fun guess(secret: String): Result<Game, Exception> {
        if (won) {
            return Failure(GameAlreadyCompleted(id))
        }
        return Success(copy(won = this.secret == secret))
    }
}
