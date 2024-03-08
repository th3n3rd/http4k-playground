package com.example.common.infra

import com.example.player.PlayerId
import org.http4k.core.Filter
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.lens.RequestContextLens

object PlayerAuthenticated {
    operator fun invoke(playerIdLens: RequestContextLens<PlayerId>, playerId: PlayerId) = AppRequestContext()
        .then(Filter {
            next -> {
                next(it.with(playerIdLens of playerId))
            }
        })
}
