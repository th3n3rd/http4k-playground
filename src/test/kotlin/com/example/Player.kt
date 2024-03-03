package com.example

import io.kotest.matchers.shouldBe
import java.util.*
import org.http4k.client.JavaHttpClient
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.debug
import org.http4k.format.Jackson.auto
import org.http4k.kotest.shouldHaveStatus

class Player(baseUri: Uri) {

    private val client = SetBaseUriFrom(baseUri)
        .then(JavaHttpClient())
        .debug()

    fun startNewGame(): GameId {
        val response = client(Request(POST, "/games"))
        return GameId(Body.auto<GameStarted>().toLens()(response).id)
    }

    fun hasWon(game: GameId) {
        val response = client(Request(GET, "/games/${game.value}"))
        val details = Body.auto<GameDetails>().toLens()(response)
        details.won shouldBe true
    }

    fun guess(game: GameId, secret: String) {
        val payload = Body.auto<Guess>().toLens()
        val response = client(
            Request(POST, "/games/${game.value}/guesses")
                .with(payload of Guess(secret))
        )
        response shouldHaveStatus CREATED
    }

    fun receivedHint(game: GameId, hint: String) {
        val response = client(Request(GET, "/games/${game.value}"))
        val details = Body.auto<GameDetails>().toLens()(response)
        details.hint shouldBe hint
    }

    data class Guess(val secret: String)
    data class GameStarted(val id: UUID)
    data class GameDetails(val hint: String, val won: Boolean)
}
