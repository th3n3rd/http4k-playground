package com.example

import com.example.common.infra.DatabaseMigrations
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.infra.InMemory
import com.example.gameplay.infra.Rotating
import com.example.player.PasswordEncoder
import com.example.player.RegisteredPlayers
import com.example.player.infra.Argon2
import com.example.player.infra.InMemory
import org.http4k.core.Uri
import org.http4k.filter.debug
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.Test

class JourneyTests {

    private val app = App(
        players = RegisteredPlayers.InMemory(),
        games = Games.InMemory(),
        secrets = Secrets.Rotating(listOf("secret")),
        passwordEncoder = PasswordEncoder.Argon2()
    ).debug()

    private val dbMigrations = DatabaseMigrations()

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
