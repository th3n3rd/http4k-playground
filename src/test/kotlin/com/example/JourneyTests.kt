package com.example

import org.http4k.core.Uri
import org.http4k.filter.debug
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.Test

class JourneyTests {

    private val app = App(
        players = InMemoryPlayers(),
        games = InMemoryGames(),
        secrets = RotatingSecrets(listOf("secret"))
    ).debug()
    private val appServer = app.asServer(SunHttp(0)).start()
    private val player = Player(Uri.of("http://localhost:${appServer.port()}"))

    @Test
    fun `winning gameplay`() {
        val newGame = player.startNewGame()

        player.receivedHint(newGame, "______")
        player.guess(newGame, "secret")

        player.hasWon(newGame)
    }
}
