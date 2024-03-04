package com.example

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RegisterNewPlayerTests {

    private val passwordEncoder = FakePasswordEncoder
    private val players = InMemoryPlayers()
    private val registerNewPlayer = RegisterNewPlayer(players, passwordEncoder)

    @Test
    fun `new players are persisted`() {
        registerNewPlayer(
            RegisterNewPlayer.Command(
            "dont-care",
            "dont-care"
        ))

        players.existsBy("dont-care", EncodedPassword("encoded-dont-care")) shouldBe true
    }
}

