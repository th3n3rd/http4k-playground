package com.example.guessing.player.infra

import com.example.guessing.player.RegisterNewPlayer
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.format.Jackson.auto

object RegisterNewPlayerApi {

    operator fun invoke(registerNewPlayer: RegisterNewPlayer): ContractRoute {
        return "/players" meta {
            summary = "Register a new player"
            operationId = "registerNewPlayer"

            receiving(Request.submittedCredentials to SubmittedCredentials("player-username", "player-password"))

            returning(CREATED to "Successful player registration")
        } bindContract POST to { req ->
            val credentials = Request.submittedCredentials(req)
            registerNewPlayer(
                RegisterNewPlayer.Command(
                    credentials.username,
                    credentials.password
                )
            )
            Response(CREATED)
        }
    }

    data class SubmittedCredentials(val username: String, val password: String)

    private object Request {
        val submittedCredentials = Body.auto<SubmittedCredentials>().toLens()
    }
}

fun RegisterNewPlayer.asRoute() = RegisterNewPlayerApi(this)
