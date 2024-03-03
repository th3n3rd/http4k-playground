package com.example

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RegisterNewPlayerTests {

    private val players = InMemoryPlayers()
    private val registerNewPlayer = RegisterNewPlayer(players)

    @Test
    fun `new players are persisted`() {
        registerNewPlayer(
            RegisterNewPlayer.Command(
            "dont-care",
            "dont-care"
        ))

        players.existsBy("dont-care", "dont-care") shouldBe true
    }
}

