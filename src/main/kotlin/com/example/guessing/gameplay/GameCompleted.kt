package com.example.guessing.gameplay

import com.example.guessing.player.PlayerId
import org.http4k.events.Event

data class GameCompleted(
    val gameId: GameId,
    val playerId: PlayerId,
    val attempts: Int
): Event