package com.example.common.infra

import com.example.player.PlayerId
import org.http4k.core.RequestContexts
import org.http4k.filter.ServerFilters.InitialiseRequestContext
import org.http4k.lens.RequestContextKey

object AppRequestContext {
    private val context = RequestContexts()

    fun authenticatedPlayerId() = RequestContextKey.required<PlayerId>(context)

    operator fun invoke() = InitialiseRequestContext(context)
}
