package com.example.guessing.player

import java.util.UUID

@JvmInline
value class PlayerId(val value: UUID = UUID.randomUUID()) {
    companion object {
        val Placeholder: PlayerId = PlayerId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    }
}
