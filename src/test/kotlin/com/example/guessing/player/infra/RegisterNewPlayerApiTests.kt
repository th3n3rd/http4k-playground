package com.example.guessing.player.infra

import com.example.guessing.common.infra.IdGenerator
import com.example.guessing.common.infra.Static
import com.example.guessing.player.PasswordEncoder
import com.example.guessing.player.RegisterNewPlayer
import com.example.guessing.player.RegisteredPlayers
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.junit.jupiter.api.Test

class RegisterNewPlayerApiTests {

    private val passwordEncoder = PasswordEncoder.Fake()
    private val players = RegisteredPlayers.InMemory()
    private val idGenerator = IdGenerator.Static()
    private val api = RegisterNewPlayer(players, passwordEncoder, idGenerator).asRoute()

    @Test
    fun `registers new players`() {
        val response = api(Request(POST, "players").body("""
        {
            "username": "dont-care",
            "password": "dont-care"
        }
        """.trimIndent()))

        with(response) {
            status shouldBe CREATED
        }
        players.findAll() shouldNot beEmpty()
    }
}
