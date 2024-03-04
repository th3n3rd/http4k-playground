package com.example

import com.example.player.InMemoryPlayers
import com.example.player.PasswordEncodings
import org.http4k.core.Uri
import org.http4k.filter.debug
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.Test

class JourneyTests {

    private val app = App(
        players = InMemoryPlayers(),
        games = InMemoryGames(),
        secrets = RotatingSecrets(listOf("secret")),
        passwordEncoder = PasswordEncodings.Argon2
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
