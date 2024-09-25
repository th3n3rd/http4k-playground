package com.example.guessing

import com.example.guessing.common.infra.IdGenerator
import com.example.guessing.common.infra.Random
import com.example.guessing.common.infra.RunDatabaseMigrations
import com.example.guessing.common.infra.ServerTracing
import com.example.guessing.gameplay.GetGameDetails
import com.example.guessing.gameplay.StartNewGame
import com.example.guessing.gameplay.SubmitGuess
import com.example.guessing.gameplay.infra.asRoute
import com.example.guessing.leaderboard.ShowLeaderboard
import com.example.guessing.leaderboard.TrackPerformances
import com.example.guessing.leaderboard.infra.asRoute
import com.example.guessing.leaderboard.infra.asTask
import com.example.guessing.player.RegisterNewPlayer
import com.example.guessing.player.infra.AuthenticatePlayer
import com.example.guessing.player.infra.PlayerRequestContext
import com.example.guessing.player.infra.PlayerRequestContext.withPlayerId
import com.example.guessing.player.infra.asRoute
import org.http4k.cloudnative.env.Environment.Companion.ENV
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
import org.http4k.server.SunHttp
import org.http4k.server.asServer

object App {
    operator fun invoke(context: AppContext = AppContext()): HttpHandler {
        with (context) {
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

            return ServerTracing(eventsBus)
                .then(Cors(corsPolicy))
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
}

fun main() {
    RunDatabaseMigrations(environment = ENV)
    App(AppContext.Prod(ENV))
        .asServer(SunHttp(9000))
        .start()
}