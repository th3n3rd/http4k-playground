@file:JvmName("ApiKt")

package com.example.gameplay.infra

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

    operator fun invoke(startNewGame: StartNewGame, authenticatedPlayerIdLens: RequestContextLens<PlayerId>): RoutingHttpHandler {
        return "/games" bind POST to {
            val newGame = startNewGame(authenticatedPlayerIdLens(it))
            Response(CREATED).with(Response.gameStarted of GameStarted(newGame.id.value))
        }
    }

    data class GameStarted(val id: UUID)

    private object Response {
        val gameStarted = Body.auto<GameStarted>().toLens()
    }
}
