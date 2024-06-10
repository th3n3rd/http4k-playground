package com.example

import com.example.common.infra.DatabaseContext
import com.example.common.infra.TracingEvents
import com.example.common.infra.ServerTracing
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.infra.Database
import com.example.gameplay.infra.Rotating
import com.example.gameplay.infra.TracingGames
import com.example.leaderboard.infra.InMemory
import com.example.leaderboard.Ranking
import com.example.leaderboard.Rankings
import com.example.player.PasswordEncoder
import com.example.player.PlayerId
import com.example.player.RegisteredPlayers
import com.example.player.infra.Argon2
import com.example.player.infra.Database
import com.example.player.infra.TracingRegisteredPlayers
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.filter.DebuggingFilters.PrintRequest
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
        val rankings = Rankings.InMemory().apply {
            save(Ranking(PlayerId(), "alice", 25))
            save(Ranking(PlayerId(), "bob", 100))
            save(Ranking(PlayerId(), "charlie", 50))
        }

        val printingApp: HttpHandler = PrintRequest()
            .then(ServerTracing(appEvents))
            .then(GuessTheSecretApp(
                players = players,
                games = games,
                secrets = secrets,
                rankings = rankings,
                passwordEncoder = PasswordEncoder.Argon2(),
            ))

        return printingApp
            .asServer(SunHttp(port))
            .start()
            .apply {
                println("Server started on " + port())
            }
    }
}

fun main() {
    StartGuessTheSecretAppServer(environment = ENV, port = 9000)
}