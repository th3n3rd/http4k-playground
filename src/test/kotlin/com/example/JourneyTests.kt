package com.example

import com.example.common.infra.RecordTraces
import com.example.common.infra.RunDatabaseMigrations
import com.example.common.infra.TestEnvironment
import org.http4k.core.Uri
import org.junit.jupiter.api.Test

class JourneyTests : RecordTraces() {

    private val env = TestEnvironment()

    init {
        RunDatabaseMigrations(env)
    }

    private val appServer = StartGuessTheSecretAppServer(
        environment = env,
        events = events
    )

    @Test
    fun `winning gameplay`() {
        val alice = newPlayer("alice")
        val newGame = alice.startNewGame()

        alice.receivedHint(newGame, "_______")

        alice.guess(newGame, "incorrect")
        alice.receivedHint(newGame, "c______")

        alice.guess(newGame, "incorrect")
        alice.receivedHint(newGame, "c_____t")

        alice.guess(newGame, "correct")

        alice.hasWon(newGame)
    }

    @Test
    fun `track performance`() {
        val alice = newPlayer("alice").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "incorrect")
            it.guess(newGame, "incorrect")
            it.guess(newGame, "incorrect")
            it.guess(newGame, "correct")
        }

        val bob = newPlayer("bob").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "correct")
        }

        val charlie = newPlayer("charlie").also {
            val newGame = it.startNewGame()
            it.guess(newGame, "incorrect")
            it.guess(newGame, "correct")
        }

        bob.checkLeaderboard(
            mapOf(
                bob.username to 100,
                charlie.username to 50,
                alice.username to 25
            )
        )
    }

    private fun newPlayer(username: String): Player {
        return Player(
            baseUri = Uri.of("http://localhost:${appServer.port()}"),
            username = username,
            password = username,
            events = events
        )
    }
}
