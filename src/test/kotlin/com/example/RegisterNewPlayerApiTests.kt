package com.example

import io.kotest.matchers.shouldBe
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.junit.jupiter.api.Test

class RegisterNewPlayerApiTests {

    private val players = InMemoryPlayers()
    private val api = RegisterNewPlayerApi(RegisterNewPlayer(players))

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
        players.existsBy("dont-care", "dont-care") shouldBe true
    }
}
