package com.example.gameplay

import dev.forkhandles.values.UUIDValue
import dev.forkhandles.values.UUIDValueFactory
import java.util.*

class GameId(value: UUID = UUID.randomUUID()): UUIDValue(value) {
    companion object: UUIDValueFactory<GameId>(::GameId)
}
