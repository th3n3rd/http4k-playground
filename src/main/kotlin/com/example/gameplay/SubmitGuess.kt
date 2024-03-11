package com.example.gameplay

import com.example.player.PlayerId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.peek

class SubmitGuess(private val games: Games) {
    operator fun invoke(gameId: GameId, secret: String, playerId: PlayerId = PlayerId()): Result<Game, Exception> {
        val game = games.findById(gameId) ?: return Failure(GameNotFound(gameId))
        if (!game.ownedBy(playerId)) {
            return Failure(GameOwnershipMismatch(gameId))
        }
        return game.guess(secret).peek {
            games.save(it)
        }
    }
}
