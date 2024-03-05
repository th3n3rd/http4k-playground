package com.example.player

import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.junit.jupiter.api.Test

class RegisterNewPlayerApiTests {

    private val passwordEncoder = FakePasswordEncoder
    private val players = RegisteredPlayers.InMemory()
    private val api = RegisterNewPlayerApi(RegisterNewPlayer(players, passwordEncoder))

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
