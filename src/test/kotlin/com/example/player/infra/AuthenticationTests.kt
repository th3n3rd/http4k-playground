package com.example.player.infra

import com.example.common.infra.AppRequestContext
import com.example.common.infra.AppRequestContext.authenticatedPlayerIdLens
import com.example.player.EncodedPassword
import com.example.player.PasswordEncoder
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import io.kotest.matchers.equals.shouldBeEqual
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
    private val validCredentials = Credentials("valid", "valid")
    private val invalidCredentials = Credentials("invalid", "invalid")
    private val registeredPlayer = RegisteredPlayer(
        username = "valid",
        password = EncodedPassword("encoded-valid")
    )

    private val authenticatedPlayerIdLens = authenticatedPlayerIdLens()
    private val dummyApi = { request: Request -> Response(OK).body(authenticatedPlayerIdLens(request).value.toString()) }
    private val protectedApi = AppRequestContext()
        .then(AuthenticatePlayer(players, PasswordEncoder.Fake(), authenticatedPlayerIdLens))
        .then(dummyApi)

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

    @Test
    fun `stores the authenticated player identifier in the context`() {
        players.save(registeredPlayer)

        val response = protectedApi(Request(GET, "/").withBasicAuth(validCredentials))

        with(response) {
            status shouldBe OK
            bodyString() shouldBeEqual registeredPlayer.id.value.toString()
        }
    }
}

