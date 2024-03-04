package com.example.player

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RegisterNewPlayerTests {

    private val passwordEncoder = FakePasswordEncoder
    private val players = InMemoryRegisteredPlayers()
    private val registerNewPlayer = RegisterNewPlayer(players, passwordEncoder)

    @Test
    fun `new players are persisted`() {
        registerNewPlayer(
            RegisterNewPlayer.Command(
            "dont-care",
            "dont-care"
        ))

        val newPlayer = players.findAll().first()
        newPlayer.username shouldBe "dont-care"
        newPlayer.password shouldBe EncodedPassword("encoded-dont-care")
    }
}

