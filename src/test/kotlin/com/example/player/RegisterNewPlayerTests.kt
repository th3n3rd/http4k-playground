package com.example.player

import com.example.common.infra.IdGenerator
import com.example.common.infra.Static
import com.example.player.infra.Fake
import com.example.player.infra.InMemory
import io.kotest.matchers.shouldBe
import org.bouncycastle.asn1.crmf.SinglePubInfo.dontCare
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

