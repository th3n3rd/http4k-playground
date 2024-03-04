@file:JvmName("ApiKt")

package com.example

import java.util.*
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind


fun StartNewGameApi(startNewGame: StartNewGame): RoutingHttpHandler {
    data class GameStarted(val id: UUID)

    val payload = Body.auto<GameStarted>().toLens()

    return "/games" bind POST to {
        val newGame = startNewGame()
        Response(CREATED).with(payload of GameStarted(newGame.id.value))
    }
}
