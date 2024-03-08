package com.example.player

import java.util.*

object Players {
    fun anyPlayer() = RegisteredPlayer(
        username = "dont-care-${UUID.randomUUID()}",
        password = EncodedPassword("dont-care")
    )
}
