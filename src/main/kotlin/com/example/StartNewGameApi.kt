package com.example

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind

private val payload = Body.auto<GameId>().toLens()

fun StartNewGameApi() = "/games" bind Method.POST to {
    Response(Status.CREATED).with(payload of GameId())
}
