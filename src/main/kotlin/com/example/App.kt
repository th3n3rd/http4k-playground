package com.example

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun App(games: Games, secrets: Secrets) = routes(
    StartNewGameApi(StartNewGame(games, secrets)),
    GetGameDetailsApi(),
    GuessApi(Guess(games))
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(App(
        InMemoryGames(),
        RotatingSecrets(listOf("secret"))
    ))

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}