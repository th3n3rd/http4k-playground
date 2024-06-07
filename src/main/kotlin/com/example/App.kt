package com.example

import com.example.common.infra.AppRequestContext
import com.example.common.infra.AppRequestContext.authenticatedPlayerIdLens
import com.example.common.infra.OriginAwareEvents
import com.example.common.infra.ServerTracing
import com.example.gameplay.*
import com.example.gameplay.infra.TracingGames
import com.example.gameplay.infra.asRoute
import com.example.player.PasswordEncoder
import com.example.player.RegisterNewPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.AuthenticatePlayer
import com.example.player.infra.TracingRegisteredPlayers
import com.example.player.infra.asRoute
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

        val appEvents = OriginAwareEvents("app", events)
        val tracingGames = TracingGames(appEvents, games)
        val tracingPlayers = TracingRegisteredPlayers(appEvents, players)

        return ServerTracing(appEvents)
            .then(AppRequestContext())
            .then(
                routes(
                    RegisterNewPlayer(tracingPlayers, passwordEncoder).asRoute(),
                    authenticatePlayer.then(StartNewGame(tracingGames, secrets).asRoute(authenticatedPlayerId)),
                    authenticatePlayer.then(GetGameDetails(tracingGames).asRoute(authenticatedPlayerId)),
                    authenticatePlayer.then(SubmitGuess(tracingGames).asRoute(authenticatedPlayerId))
                )
            )
    }
}