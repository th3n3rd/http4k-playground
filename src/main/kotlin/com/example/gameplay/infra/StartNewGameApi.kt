@file:JvmName("ApiKt")

package com.example.gameplay.infra

import com.example.gameplay.StartNewGame
import java.util.*
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object StartNewGameApi {
    private val payload = Body.auto<GameStarted>().toLens()

    operator fun invoke(startNewGame: StartNewGame): RoutingHttpHandler {
        return "/games" bind POST to {
            val newGame = startNewGame()
            Response(CREATED).with(payload of GameStarted(newGame.id.value))
        }
    }

    data class GameStarted(val id: UUID)
}
