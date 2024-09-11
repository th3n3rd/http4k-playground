package com.example.guessing.player.infra

import com.example.guessing.player.infra.PlayerRequestContext.withPlayerId
import com.example.guessing.player.PlayerId
import com.example.guessing.player.infra.PlayerRequestContext
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.core.with

fun HttpHandler.authenticatedAs(playerId: PlayerId) = PlayerRequestContext()
    .then(Filter {
        next -> {
            next(it.with(withPlayerId of playerId))
        }
    })
    .then(this)


