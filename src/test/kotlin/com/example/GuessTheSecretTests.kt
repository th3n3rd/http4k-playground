package com.example

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class Player(val http: HttpHandler) {
    fun startNewGame(): GameId {
        val response = http(Request(POST, "/games"))
        return Body.auto<GameId>().toLens()(response)
    }

    fun hasWon(game: GameId): Boolean {
        val response = http(Request(GET, "/games/${game.value}"))
        val details = Body.auto<GameDetails>().toLens()(response)
        return details.won
    }
}

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
