package com.example

import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

fun RegisterNewPlayerApi(registerNewPlayer: RegisterNewPlayer): RoutingHttpHandler {
    data class SubmittedCredentials(val username: String, val password: String)

    val submittedCredentials = Body.auto<SubmittedCredentials>().toLens()

    return "/players" bind POST to {
        val credentials = submittedCredentials(it)
        registerNewPlayer(RegisterNewPlayer.Command(
            credentials.username,
            credentials.password
        ))
        Response(CREATED)
    }
}
