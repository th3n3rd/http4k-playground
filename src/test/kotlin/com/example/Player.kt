package com.example

import com.example.gameplay.GameId
import io.kotest.matchers.shouldBe
import java.util.*
import org.http4k.client.JavaHttpClient
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.BasicAuth
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.debug
import org.http4k.format.Jackson.auto

class Player(
    baseUri: Uri,
    username: String = "",
    password: String = ""
) {
    private val credentialsLens = Body.auto<Credentials>().toLens()
    private val gameStartedLens = Body.auto<GameStarted>().toLens()
    private val gameDetailsLens = Body.auto<GameDetails>().toLens()
    private val guessLens = Body.auto<Guess>().toLens()

    private val client = SetBaseUriFrom(baseUri)
        .then(BasicAuth(user = username, password = password))
        .then(JavaHttpClient())
        .debug()

    init {
        register(username, password)
    }

    fun startNewGame(): GameId {
        val response = client(Request(POST, "/games"))
        response.status.successful shouldBe true
        return GameId(gameStartedLens(response).id)
    }

    fun hasWon(game: GameId) {
        val response = client(Request(GET, "/games/${game.value}"))
        response.status.successful shouldBe true
        val details = gameDetailsLens(response)
        details.won shouldBe true
    }

    fun guess(game: GameId, secret: String) {
        val response = client(
            Request(POST, "/games/${game.value}/guesses")
                .with(guessLens of Guess(secret))
        )
        response.status.successful shouldBe true
    }

    fun receivedHint(game: GameId, hint: String) {
        val response = client(Request(GET, "/games/${game.value}"))
        response.status.successful shouldBe true
        val details = gameDetailsLens(response)
        details.hint shouldBe hint
    }

    private fun register(username: String, password: String) {
        val response = client(
            Request(POST, "/players")
                .with(credentialsLens of Credentials(username, password))
        )
        response.status.successful shouldBe true
    }

    data class Credentials(val username: String, val password: String)
    data class Guess(val secret: String)
    data class GameStarted(val id: UUID)
    data class GameDetails(val hint: String, val won: Boolean)
}
