package com.example

import com.example.common.infra.DatabaseContext
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.infra.Database
import com.example.gameplay.infra.Rotating
import com.example.player.PasswordEncoder
import com.example.player.RegisteredPlayers
import com.example.player.infra.Argon2
import com.example.player.infra.Database
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.filter.DebuggingFilters
import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer

object StartAppServer {
    operator fun invoke(environment: Environment, events: Events = {}, port: Int = 0): Http4kServer {
        val database = DatabaseContext(environment)

        val printingApp: HttpHandler = DebuggingFilters.PrintRequest().then(
            App(
                players = RegisteredPlayers.Database(database),
                games = Games.Database(database),
                secrets = Secrets.Rotating(listOf("correct")),
                passwordEncoder = PasswordEncoder.Argon2(),
                events = events
            )
        )

        return printingApp
            .asServer(SunHttp(port))
            .start()
            .apply {
                println("Server started on " + port())
            }
    }
}

fun main() {
    StartAppServer(environment = ENV, port = 9000)
}