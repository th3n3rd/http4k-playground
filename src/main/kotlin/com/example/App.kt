package com.example

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun App(players: Players, games: Games, secrets: Secrets, passwordEncoder: PasswordEncoder): HttpHandler {

    val authenticatePlayer = AuthenticatePlayer(players, passwordEncoder)

    return routes(
        RegisterNewPlayerApi(RegisterNewPlayer(players, passwordEncoder)),
        authenticatePlayer.then(StartNewGameApi(StartNewGame(games, secrets))),
        authenticatePlayer.then(GetGameDetailsApi(games)),
        authenticatePlayer.then(SubmitGuessApi(SubmitGuess(games)))
    )
}

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(App(
        players = InMemoryPlayers(),
        games = InMemoryGames(),
        secrets = RotatingSecrets(listOf("secret")),
        passwordEncoder = PasswordEncodings.Argon2
    ))

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}
