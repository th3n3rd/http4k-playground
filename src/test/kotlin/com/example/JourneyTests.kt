package com.example

import com.example.common.infra.RecordTraces
import com.example.common.infra.RunDatabaseMigrations
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.core.Uri
import org.junit.jupiter.api.Test
import kotlin.random.Random

class JourneyTests: RecordTraces() {

    init {
        RunDatabaseMigrations(ENV)
    }

    private val appServer = StartGuessTheSecretAppServer(
        environment = ENV,
        events = events
    )

    private val player = newPlayer()

    @Test
    fun `winning gameplay`() {
        val newGame = player.startNewGame()
        player.receivedHint(newGame, "_______")

        player.guess(newGame, "incorrect")
        player.receivedHint(newGame, "c______")

        player.guess(newGame, "incorrect")
        player.receivedHint(newGame, "c_____t")

        player.guess(newGame, "correct")

        player.hasWon(newGame)
    }

    private fun newPlayer(): Player {
        val randomCredentials = "player-${Random.nextInt()}"
        return Player(
            baseUri = Uri.of("http://localhost:${appServer.port()}"),
            username = randomCredentials,
            password = randomCredentials,
            events = events
        )
    }
}
