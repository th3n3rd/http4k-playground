package com.example

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
        return Body.auto<GameId>().toLens()(response)
    }

    fun hasWon(game: GameId): Boolean {
        val response = client(Request(GET, "/games/${game.value}"))
        val details = Body.auto<GameDetails>().toLens()(response)
        return details.won
    }

    data class SubmittedGuess(val secret: String)

    fun guess(game: GameId, secret: String) {
        val payload = Body.auto<SubmittedGuess>().toLens()
        val response = client(
            Request(POST, "/games/${game.value}/guesses")
                .with(payload of SubmittedGuess(secret))
        )
        response shouldHaveStatus CREATED
    }
}
