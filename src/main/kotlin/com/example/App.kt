package com.example

import com.example.common.infra.AppRequestContext
import com.example.common.infra.AppRequestContext.withPlayerId
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
import com.example.player.infra.protectedBy
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
        val authentication = AuthenticatePlayer(players, passwordEncoder, withPlayerId)

        val appEvents = OriginAwareEvents("app", events)
        val tracingGames = TracingGames(appEvents, games)
        val tracingPlayers = TracingRegisteredPlayers(appEvents, players)

        return ServerTracing(appEvents)
            .then(AppRequestContext())
            .then(
                routes(
                    RegisterNewPlayer(tracingPlayers, passwordEncoder)
                        .asRoute(),
                    StartNewGame(tracingGames, secrets)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    GetGameDetails(tracingGames)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    SubmitGuess(tracingGames)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication)
                )
            )
    }
}