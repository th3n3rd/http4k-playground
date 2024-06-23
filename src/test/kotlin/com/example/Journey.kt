package com.example

import org.http4k.core.Uri
import org.http4k.events.Events

interface Journey {
    val events: Events
    val appUri: Uri

    fun newPlayer(username: String): Player {
        return Player(
            baseUri = appUri,
            username = username,
            password = username,
            events = events
        )
    }
}