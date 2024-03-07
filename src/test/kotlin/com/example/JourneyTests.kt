package com.example

import com.example.common.infra.DatabaseContext
import com.example.common.infra.RunDatabaseMigrations
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.infra.Database
import com.example.gameplay.infra.Rotating
import com.example.player.PasswordEncoder
import com.example.player.RegisteredPlayers
import com.example.player.infra.Argon2
import com.example.player.infra.Database
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.core.Uri
import org.http4k.filter.debug
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.Test

class JourneyTests {

    init {
        RunDatabaseMigrations(ENV)
    }

    private val database = DatabaseContext(ENV)

    private val app = App(
        players = RegisteredPlayers.Database(database),
        games = Games.Database(database),
        secrets = Secrets.Rotating(listOf("secret")),
        passwordEncoder = PasswordEncoder.Argon2()
    ).debug()

    private val appServer = app.asServer(SunHttp(0)).start()

    private val player = Player(
        baseUri = Uri.of("http://localhost:${appServer.port()}"),
        username = "player-1",
        password = "player-1"
    )

    @Test
    fun `winning gameplay`() {
        val newGame = player.startNewGame()

        player.receivedHint(newGame, "______")
        player.guess(newGame, "secret")

        player.hasWon(newGame)
    }
}
