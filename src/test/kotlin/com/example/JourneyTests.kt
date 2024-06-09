package com.example

import com.example.common.infra.RecordTraces
import com.example.common.infra.RunDatabaseMigrations
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.core.Uri
import org.junit.jupiter.api.Test
import kotlin.random.Random

class JourneyTests : RecordTraces() {

    init {
        RunDatabaseMigrations(ENV)
    }

    private val appServer = StartGuessTheSecretAppServer(
        environment = ENV,
        events = events
    )

    @Test
    fun `winning gameplay`() {
        val player = newPlayer()
        val newGame = player.startNewGame()

        player.receivedHint(newGame, "_______")

        player.guess(newGame, "incorrect")
        player.receivedHint(newGame, "c______")

        player.guess(newGame, "incorrect")
        player.receivedHint(newGame, "c_____t")

        player.guess(newGame, "correct")

        player.hasWon(newGame)
    }

    @Test
    fun `track performance`() {
        val firstPlayer = newPlayer("player-1").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "incorrect")
            it.guess(newGame, "incorrect")
            it.guess(newGame, "incorrect")
            it.guess(newGame, "correct")
        }

        val secondPlayer = newPlayer("player-2").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "correct")
        }

        val thirdPlayer = newPlayer("player-3").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "incorrect")
            it.guess(newGame, "correct")
        }

        secondPlayer.checkLeaderboard(
            mapOf(
                secondPlayer.username to 100,
                thirdPlayer.username to 50,
                firstPlayer.username to 25
            )
        )
    }

    private fun newPlayer(username: String = "player-${Random.nextInt()}"): Player {
        return Player(
            baseUri = Uri.of("http://localhost:${appServer.port()}"),
            username = username,
            password = username,
            events = events
        )
    }
}
