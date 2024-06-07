package com.example.common.infra

import com.example.common.infra.AppRequestContext.withPlayerId
import com.example.player.PlayerId
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.core.with

fun HttpHandler.authenticatedAs(playerId: PlayerId) = AppRequestContext()
    .then(Filter {
        next -> {
            next(it.with(withPlayerId of playerId))
        }
    })
    .then(this)


