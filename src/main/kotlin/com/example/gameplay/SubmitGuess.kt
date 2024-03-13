package com.example.gameplay

import com.example.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.peek

class SubmitGuess(private val games: Games) {
    operator fun invoke(gameId: GameId, secret: String, playerId: PlayerId): Result<Game, Exception> {
        val game = games.findByIdAndPlayerId(gameId, playerId) ?: return Failure(GameNotFound(gameId))
        return game.guess(secret).peek {
            games.save(it)
        }
    }
}
