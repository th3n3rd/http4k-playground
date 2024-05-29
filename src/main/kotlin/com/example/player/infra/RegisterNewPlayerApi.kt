package com.example.player.infra

import com.example.player.RegisterNewPlayer
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object RegisterNewPlayerApi {

    operator fun invoke(registerNewPlayer: RegisterNewPlayer): RoutingHttpHandler {
        return "/players" bind POST to {
            val credentials = Request.submittedCredentials(it)
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
