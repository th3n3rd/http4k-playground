package com.example.guessing.player.infra

import com.example.guessing.player.PlayerId
import org.http4k.core.RequestContexts
import org.http4k.filter.ServerFilters.InitialiseRequestContext
import org.http4k.lens.RequestContextKey

object PlayerRequestContext {
    private val context = RequestContexts("player")

    val withPlayerId = RequestContextKey.required<PlayerId>(context, name = "authenticated-player-id")

    operator fun invoke() = InitialiseRequestContext(context)
}
