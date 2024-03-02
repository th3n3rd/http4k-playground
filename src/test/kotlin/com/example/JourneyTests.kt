package com.example

import org.http4k.core.Uri
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.Test

class JourneyTests {

    private val app = App(
        InMemoryGames(),
        RotatingSecrets(listOf("secret"))
    )
    private val appServer = app.asServer(SunHttp(0)).start()
    private val player = Player(Uri.of("http://localhost:${appServer.port()}"))

    @Test
    fun `winning gameplay`() {
        val newGame = player.startNewGame()

        player.guess(newGame, "secret")

        player.hasWon(newGame)
    }
}
