package com.example

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun App() = routes(
    PingApi(),
    StartNewGameApi(),
    GetGameDetailsApi()
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(App())

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}
