package com.example.guessing

import com.example.guessing.common.infra.EventsBus
import com.example.guessing.common.infra.IdGenerator
import com.example.guessing.common.infra.Random
import com.example.guessing.gameplay.*
import com.example.guessing.gameplay.infra.asRoute
import com.example.guessing.leaderboard.Rankings
import com.example.guessing.leaderboard.ShowLeaderboard
import com.example.guessing.leaderboard.TrackPerformances
import com.example.guessing.leaderboard.infra.asRoute
import com.example.guessing.leaderboard.infra.asTask
import com.example.guessing.player.PasswordEncoder
import com.example.guessing.player.RegisterNewPlayer
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.infra.AuthenticatePlayer
import com.example.guessing.player.infra.PlayerRequestContext
import com.example.guessing.player.infra.PlayerRequestContext.withPlayerId
import com.example.guessing.player.infra.asRoute
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.ApiServer
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.HttpHandler
import org.http4k.core.Method.*
import org.http4k.core.Uri
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
            listOf("content-type", "authorization"),
            listOf(GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD),
            true
        )

        val idGenerator = IdGenerator.Random()

        return Cors(corsPolicy)
            .then(PlayerRequestContext())
            .then(
                routes(
                    contract {
                        renderer = OpenApi3(
                            apiInfo = ApiInfo("guess the secret app", "v1"),
                            servers = listOf(ApiServer(Uri.of("http://localhost:9000")))
                        )
                        descriptionPath = "/docs/openapi.json"
                        routes += setOf(
                            RegisterNewPlayer(players, passwordEncoder, idGenerator).asRoute(),
                            ShowLeaderboard().asRoute(rankings, authentication),
                            SubmitGuess(games, eventsBus).asRoute(withPlayerId, authentication),
                            StartNewGame(games, secrets, idGenerator).asRoute(withPlayerId, authentication),
                            GetGameDetails(games).asRoute(withPlayerId, authentication)
                        )
                    }
                )
            )
    }
}