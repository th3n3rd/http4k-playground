package com.example.guessing

import com.example.guessing.common.infra.RunDatabaseMigrations
import com.example.guessing.common.infra.ServerTracing
import com.example.guessing.common.infra.TracingEvents
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
        val appEvents = TracingEvents("app", events)

        val app: HttpHandler = ServerTracing(appEvents)
            .then(
                GuessTheSecretApp(
                    GuessTheSecretAppContext.Prod(environment, events),
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