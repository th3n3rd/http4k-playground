package com.example.leaderboard

import com.example.player.PlayerId
import com.example.player.RegisteredPlayers
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

class TrackPerformances(
    private val players: RegisteredPlayers,
    private val rankings: Rankings
) {
    operator fun invoke(playerId: PlayerId, attempts: Int): Result<Unit, TrackPerformanceError> {
        val player = players.findById(playerId)
            ?: return Failure(TrackPerformanceError.PlayerNotFound(playerId))
        val score = 100 / attempts
        val ranking = rankings.findByPlayerId(playerId)
            ?.incrementScoreBy(score)
            ?: Ranking(
                playerId = playerId,
                playerUsername = player.username,
                score = score
            )
        rankings.save(ranking)
        return Success(Unit)
    }
}

sealed interface TrackPerformanceError {
    data class PlayerNotFound(val playerId: PlayerId): RuntimeException("Player with id ${playerId.value} not found"), TrackPerformanceError
}