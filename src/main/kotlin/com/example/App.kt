package com.example

import com.example.common.infra.AppRequestContext
import com.example.common.infra.AppRequestContext.authenticatedPlayerIdLens
import com.example.common.infra.DatabaseContext
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.StartNewGame
import com.example.gameplay.SubmitGuess
import com.example.gameplay.infra.Database
import com.example.gameplay.infra.GetGameDetailsApi
import com.example.gameplay.infra.Rotating
import com.example.gameplay.infra.StartNewGameApi
import com.example.gameplay.infra.SubmitGuessApi
import com.example.player.PasswordEncoder
import com.example.player.RegisterNewPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.Argon2
import com.example.player.infra.AuthenticatePlayer
import com.example.player.infra.Database
import com.example.player.infra.RegisterNewPlayerApi
import org.http4k.cloudnative.env.Environment.Companion.ENV
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
        val authenticatedPlayerId = authenticatedPlayerIdLens()
        val authenticatePlayer = AuthenticatePlayer(players, passwordEncoder, authenticatedPlayerId)

        return AppRequestContext()
            .then(
                routes(
                    RegisterNewPlayerApi(RegisterNewPlayer(players, passwordEncoder)),
                    authenticatePlayer.then(StartNewGameApi(StartNewGame(games, secrets), authenticatedPlayerId)),
                    authenticatePlayer.then(GetGameDetailsApi(games, authenticatedPlayerId)),
                    authenticatePlayer.then(SubmitGuessApi(SubmitGuess(games), authenticatedPlayerId))
                )
            )
    }
}

fun main() {
    val database = DatabaseContext(ENV)

    val printingApp: HttpHandler = PrintRequest().then(
        App(
            players = RegisteredPlayers.Database(database),
            games = Games.Database(database),
            secrets = Secrets.Rotating(listOf("secret")),
            passwordEncoder = PasswordEncoder.Argon2()
        )
    )

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}
