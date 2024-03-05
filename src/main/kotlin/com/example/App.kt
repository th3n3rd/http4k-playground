package com.example

import com.example.gameplay.Games
import com.example.gameplay.GetGameDetailsApi
import com.example.gameplay.InMemory
import com.example.gameplay.Rotating
import com.example.gameplay.Secrets
import com.example.gameplay.StartNewGame
import com.example.gameplay.StartNewGameApi
import com.example.gameplay.SubmitGuess
import com.example.gameplay.SubmitGuessApi
import com.example.player.AuthenticatePlayer
import com.example.player.InMemory
import com.example.player.PasswordEncoder
import com.example.player.PasswordEncodings
import com.example.player.RegisterNewPlayer
import com.example.player.RegisterNewPlayerApi
import com.example.player.RegisteredPlayers
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

object App {
    operator fun invoke(players: RegisteredPlayers, games: Games, secrets: Secrets, passwordEncoder: PasswordEncoder): HttpHandler {
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
        passwordEncoder = PasswordEncodings.Argon2
    ))

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}
