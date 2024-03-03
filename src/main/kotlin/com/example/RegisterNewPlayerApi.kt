package com.example

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind

private data class SubmittedCredentials(val username: String, val password: String)
private val submittedCredentials = Body.auto<SubmittedCredentials>().toLens()

fun RegisterNewPlayerApi(registerNewPlayer: RegisterNewPlayer) = "/players" bind Method.POST to {
    val credentials = submittedCredentials(it)
    registerNewPlayer(RegisterNewPlayer.Command(
        credentials.username,
        credentials.password
    ))
    Response(CREATED)
}
