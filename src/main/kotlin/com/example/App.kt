package com.example

import com.example.common.infra.AppRequestContext
import com.example.common.infra.AppRequestContext.authenticatedPlayerIdLens
import com.example.common.infra.DatabaseContext
import com.example.common.infra.NameEvents
import com.example.common.infra.ServerTracing
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.StartNewGame
import com.example.gameplay.SubmitGuess
import com.example.gameplay.infra.GetGameDetailsApi
import com.example.gameplay.infra.StartNewGameApi
import com.example.gameplay.infra.SubmitGuessApi
import com.example.player.PasswordEncoder
import com.example.player.RegisterNewPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.AuthenticatePlayer
import com.example.player.infra.RegisterNewPlayerApi
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.routing.routes

object App {
    operator fun invoke(
        players: RegisteredPlayers,
        games: Games,
        secrets: Secrets,
        passwordEncoder: PasswordEncoder,
        events: Events
    ): HttpHandler {
        val authenticatedPlayerId = authenticatedPlayerIdLens()
        val authenticatePlayer = AuthenticatePlayer(players, passwordEncoder, authenticatedPlayerId)

        return ServerTracing(NameEvents("app", events))
            .then(AppRequestContext())
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