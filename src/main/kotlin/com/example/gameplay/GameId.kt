package com.example.gameplay

import java.util.*

@JvmInline
value class GameId(val value: UUID = UUID.randomUUID()) {
    companion object {
        val Placeholder: GameId = GameId(UUID.fromString("00000000-0000-0000-0000-000000000000"))

        fun parse(gameId: String) = GameId(UUID.fromString(gameId))
    }
}
