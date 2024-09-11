package com.example.guessing

import com.example.guessing.gameplay.Games
import com.example.guessing.gameplay.Secrets
import com.example.guessing.gameplay.infra.Database
import com.example.guessing.gameplay.infra.Rotating
import com.example.guessing.gameplay.infra.TracingGames
import com.example.guessing.common.infra.*
import com.example.guessing.leaderboard.Rankings
import com.example.guessing.leaderboard.infra.InMemory
import com.example.guessing.leaderboard.infra.TracingRankings
import com.example.guessing.player.PasswordEncoder
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.infra.Argon2
import com.example.guessing.player.infra.Database
import com.example.guessing.player.infra.TracingRegisteredPlayers
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer

object StartGuessTheSecretAppServer {
    operator fun invoke(environment: Environment, events: Events = {}, port: Int = 0): Http4kServer {
        val database = DatabaseContext(environment)
        val appEvents = TracingEvents("app", events)

        val games = TracingGames(appEvents, Games.Database(database))
        val players = TracingRegisteredPlayers(appEvents, RegisteredPlayers.Database(database))
        val secrets = Secrets.Rotating(listOf("correct"))
        val rankings = TracingRankings(appEvents, Rankings.InMemory())
        val eventsBus = EventsBus.InMemory(appEvents)

        val app: HttpHandler = ServerTracing(appEvents)
            .then(
                GuessTheSecretApp(
                    eventsBus = eventsBus,
                    players = players,
                    games = games,
                    secrets = secrets,
                    rankings = rankings,
                    passwordEncoder = PasswordEncoder.Argon2(),
                )
            )

        return app
            .asServer(SunHttp(port))
            .start()
            .apply {
                println("Server started on " + port())
            }
    }
}

fun main() {
    RunDatabaseMigrations(environment = ENV)
    StartGuessTheSecretAppServer(environment = ENV, port = 9000)
}