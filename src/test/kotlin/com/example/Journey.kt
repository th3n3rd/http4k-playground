package com.example

import org.http4k.core.Uri
import org.http4k.events.Events
import kotlin.random.Random

interface Journey {
    val events: Events
    val appUri: Uri
    val randomisePlayerUsername: Boolean

    fun newPlayer(username: String): Player {
        val playerUsername = username + randomUsernameSuffix()
        return Player(
            baseUri = appUri,
            username = playerUsername,
            password = playerUsername,
            events = events
        )
    }

    fun randomUsernameSuffix() = if (randomisePlayerUsername)
        Random.nextInt(0, 10_000)
    else ""
}