package com.example

import com.example.common.infra.EventsBus
import com.example.gameplay.*
import com.example.gameplay.infra.asRoute
import com.example.leaderboard.Rankings
import com.example.leaderboard.ShowLeaderboard
import com.example.leaderboard.TrackPerformances
import com.example.leaderboard.infra.asRoute
import com.example.leaderboard.infra.asTask
import com.example.player.PasswordEncoder
import com.example.player.RegisterNewPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.AuthenticatePlayer
import com.example.player.infra.PlayerRequestContext
import com.example.player.infra.PlayerRequestContext.withPlayerId
import com.example.player.infra.asRoute
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.HttpHandler
import org.http4k.core.Method.*
import org.http4k.core.then
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.Pattern
import org.http4k.filter.ServerFilters.Cors
import org.http4k.routing.routes

object GuessTheSecretApp {
    operator fun invoke(
        eventsBus: EventsBus,
        players: RegisteredPlayers,
        games: Games,
        secrets: Secrets,
        rankings: Rankings,
        passwordEncoder: PasswordEncoder
    ): HttpHandler {
        val authentication = AuthenticatePlayer(players, passwordEncoder, withPlayerId)

        TrackPerformances(players, rankings)
            .asTask(eventsBus)

        val corsPolicy = CorsPolicy(
            OriginPolicy.Pattern(Regex("http(s)?://localhost(:[0-9]+)?")),
            listOf("content-type"),
            listOf(GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD),
            true
        )

        return Cors(corsPolicy)
            .then(PlayerRequestContext())
            .then(
                routes(
                    contract {
                        renderer = OpenApi3(ApiInfo("guess the secret app", "v1"))
                        descriptionPath = "/docs/openapi.json"
                        routes += setOf(
                            RegisterNewPlayer(players, passwordEncoder).asRoute(),
                            ShowLeaderboard().asRoute(rankings, authentication),
                            SubmitGuess(games, eventsBus).asRoute(withPlayerId, authentication),
                            StartNewGame(games, secrets).asRoute(withPlayerId, authentication),
                            GetGameDetails(games).asRoute(withPlayerId, authentication)
                        )
                    }
                )
            )
    }
}