package com.example.guessing.player

import com.example.guessing.common.infra.IdGenerator
import com.example.guessing.common.infra.Static
import com.example.guessing.player.infra.Fake
import com.example.guessing.player.infra.InMemory
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RegisterNewPlayerTests {

    private val passwordEncoder = PasswordEncoder.Fake()
    private val players = RegisteredPlayers.InMemory()
    private val idGenerator = IdGenerator.Static(PlayerId.Placeholder.value)
    private val registerNewPlayer = RegisterNewPlayer(players, passwordEncoder, idGenerator)

    @Test
    fun `new players are persisted`() {
        registerNewPlayer(
            RegisterNewPlayer.Command(
            "dont-care",
            "dont-care"
        ))

        val newPlayer = players.findAll().first()
        newPlayer.id shouldBe PlayerId.Placeholder
        newPlayer.username shouldBe "dont-care"
        newPlayer.password shouldBe EncodedPassword("encoded-dont-care")
    }
}

