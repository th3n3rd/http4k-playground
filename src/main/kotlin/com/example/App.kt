package com.example

import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.StartNewGame
import com.example.gameplay.SubmitGuess
import com.example.gameplay.infra.GetGameDetailsApi
import com.example.gameplay.infra.InMemory
import com.example.gameplay.infra.Rotating
import com.example.gameplay.infra.StartNewGameApi
import com.example.gameplay.infra.SubmitGuessApi
import com.example.player.PasswordEncoder
import com.example.player.RegisterNewPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.Argon2
import com.example.player.infra.AuthenticatePlayer
import com.example.player.infra.InMemory
import com.example.player.infra.RegisterNewPlayerApi
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

object App {
    operator fun invoke(
        players: RegisteredPlayers,
        games: Games,
        secrets: Secrets,
        passwordEncoder: PasswordEncoder
    ): HttpHandler {
        val authenticatePlayer = AuthenticatePlayer(players, passwordEncoder)

        return routes(
            RegisterNewPlayerApi(RegisterNewPlayer(players, passwordEncoder)),
            authenticatePlayer.then(StartNewGameApi(StartNewGame(games, secrets))),
            authenticatePlayer.then(GetGameDetailsApi(games)),
            authenticatePlayer.then(SubmitGuessApi(SubmitGuess(games)))
        )
    }
}

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(App(
        players = RegisteredPlayers.InMemory(),
        games = Games.InMemory(),
        secrets = Secrets.Rotating(listOf("secret")),
        passwordEncoder = PasswordEncoder.Argon2()
    ))

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}
