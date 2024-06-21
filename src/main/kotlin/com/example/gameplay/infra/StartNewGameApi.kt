@file:JvmName("ApiKt")

package com.example.gameplay.infra

import com.example.gameplay.StartNewGame
import com.example.player.PlayerId
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.contract.security.NoSecurity
import org.http4k.contract.security.Security
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.RequestContextLens
import java.util.*

object StartNewGameApi {

    operator fun invoke(
        startNewGame: StartNewGame,
        withPlayerId: RequestContextLens<PlayerId>,
        authentication: Security
    ): ContractRoute {
        return "/games" meta {
            security = authentication
            summary = "Start a new game"
            returning(CREATED to "Successful start of a new game")
        } bindContract POST to { req ->
            val newGame = startNewGame(withPlayerId(req))
            Response(CREATED).with(Response.gameStarted of GameStarted(newGame.id.value))
        }
    }

    data class GameStarted(val id: UUID)

    private object Response {
        val gameStarted = Body.auto<GameStarted>().toLens()
    }
}

fun StartNewGame.asRoute(withPlayerId: RequestContextLens<PlayerId>, authentication: Security = NoSecurity) =
    StartNewGameApi(this, withPlayerId, authentication)