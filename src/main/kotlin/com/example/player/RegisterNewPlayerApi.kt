package com.example.player

import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object RegisterNewPlayerApi {
    private val submittedCredentials = Body.auto<SubmittedCredentials>().toLens()

    operator fun invoke(registerNewPlayer: RegisterNewPlayer): RoutingHttpHandler {
        return "/players" bind POST to {
            val credentials = submittedCredentials(it)
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
}
