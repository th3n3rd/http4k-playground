@file:JvmName("ApiKt")

package com.example.gameplay.infra

import com.example.common.infra.AppRequestContext
import com.example.gameplay.StartNewGame
import com.example.player.PlayerId
import java.util.*
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object StartNewGameApi {
    private val payload = Body.auto<GameStarted>().toLens()

    operator fun invoke(startNewGame: StartNewGame, authenticatedPlayerId: RequestContextLens<PlayerId>): RoutingHttpHandler {
        return "/games" bind POST to {
            val newGame = startNewGame(authenticatedPlayerId(it))
            Response(CREATED).with(payload of GameStarted(newGame.id.value))
        }
    }

    data class GameStarted(val id: UUID)
}
