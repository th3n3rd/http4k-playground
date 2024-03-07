package com.example.player

import java.util.UUID

@JvmInline
value class PlayerId(val value: UUID = UUID.randomUUID())
