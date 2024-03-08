package com.example.common.infra

import com.example.common.infra.AppRequestContext.authenticatedPlayerIdLens
import com.example.player.PlayerId
import org.http4k.core.Filter
import org.http4k.core.then
import org.http4k.core.with

object PlayerAuthenticated {
    val playerIdLens = authenticatedPlayerIdLens()

    operator fun invoke(playerId: PlayerId) = AppRequestContext()
        .then(Filter {
            next -> {
                next(it.with(playerIdLens of playerId))
            }
        })
}
