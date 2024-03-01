package com.example

import org.http4k.core.Uri
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GuessTheSecretTests {

    private val app = App(InMemoryGames()).asServer(SunHttp(0)).start()
    private val player = Player(Uri.of("http://localhost:${app.port()}"))

    @Test
    fun `winning gameplay`() {
        val newGame = player.startNewGame()

        assertTrue(player.hasWon(newGame));
    }
}
