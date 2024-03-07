package com.example.gameplay

import java.util.*

@JvmInline
value class GameId(val value: UUID = UUID.randomUUID()) {
    companion object {
        fun parse(gameId: String) = GameId(UUID.fromString(gameId))
    }
}
