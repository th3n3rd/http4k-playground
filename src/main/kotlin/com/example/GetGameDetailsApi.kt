package com.example

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind

private val gameId = Path.value(GameId).of("id")
private val payload = Body.auto<GameDetails>().toLens()

fun GetGameDetailsApi() = "/games/{id}" bind Method.GET to {
    Response(Status.OK).with(
        payload of GameDetails(
            id = gameId(it),
            won = true
        )
    )
}
