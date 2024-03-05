package com.example.player

import io.kotest.matchers.shouldBe
import org.http4k.core.Credentials
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.then
import org.http4k.filter.ClientFilters.CustomBasicAuth.withBasicAuth
import org.junit.jupiter.api.Test

class AuthenticationTests {

    private val players = RegisteredPlayers.InMemory()
    private val passwordEncoder = PasswordEncoder.Fake()
    private val handler = { _: Request -> Response(OK) }
    private val protectedApi = AuthenticatePlayer(players, passwordEncoder).then(handler)
    private val validCredentials = Credentials("valid", "valid")
    private val invalidCredentials = Credentials("invalid", "invalid")
    private val registeredPlayer = RegisteredPlayer(
        validCredentials.user,
        passwordEncoder(Password(validCredentials.password))
    )

    @Test
    fun `fails when the player credentials are missing`() {
        players.save(registeredPlayer)

        val response = protectedApi(Request(GET, "/"))

        with(response) {
            status shouldBe UNAUTHORIZED
        }
    }

    @Test
    fun `fails when the player credentials are incorrect`() {
        players.save(registeredPlayer)

        val response = protectedApi(Request(GET, "/").withBasicAuth(invalidCredentials))

        with(response) {
            status shouldBe UNAUTHORIZED
        }
    }

    @Test
    fun `succeeds when the player credentials are correct`() {
        players.save(registeredPlayer)

        val response = protectedApi(Request(GET, "/").withBasicAuth(validCredentials))

        with(response) {
            status shouldBe OK
        }
    }
}

