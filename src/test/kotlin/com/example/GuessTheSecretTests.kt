package com.example

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GuessTheSecretTests {

    val app = App()
    val player = Player(app)

    @Test
    fun `ping test`() {
        assertEquals(Response(OK).body("pong"), app(Request(GET, "/ping")))
    }

    @Test
    fun `winning gameplay`() {
        val newGame = player.startNewGame()

        assertTrue(player.hasWon(newGame));
    }
}
