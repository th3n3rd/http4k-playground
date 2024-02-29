package com.example

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

private val gameIdParam = Path.value(GameId).of("id")
private val gameIdPayload = Body.auto<GameId>().toLens()
private val gameDetailsPayload = Body.auto<GameDetails>().toLens()

fun App() = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },
    "/games" bind POST to {
        Response(CREATED).with(gameIdPayload of GameId())
    },
    "/games/{id}" bind GET to {
        Response(OK).with(gameDetailsPayload of GameDetails(
            id = gameIdParam(it),
            won = true
        ))
    }
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(App())

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}
