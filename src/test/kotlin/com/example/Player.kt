package com.example

import com.example.common.infra.ClientTracing
import com.example.common.infra.TracingEvents
import com.example.gameplay.GameId
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import org.http4k.client.JavaHttpClient
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.events.Events
import org.http4k.filter.ClientFilters.BasicAuth
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.debug
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutedRequest
import java.util.*

class Player(
    baseUri: Uri,
    val username: String = "",
    password: String = "",
    events: Events
) {
    private val credentialsLens = Body.auto<Credentials>().toLens()
    private val gameStartedLens = Body.auto<GameStarted>().toLens()
    private val gameDetailsLens = Body.auto<GameDetails>().toLens()
    private val guessLens = Body.auto<Guess>().toLens()
    private val leaderboardLens = Body.auto<Leaderboard>().toLens()

    private val client = ClientTracing(TracingEvents(username, events))
        .then(SetBaseUriFrom(baseUri))
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
        val response = client(RoutedRequest(Request(GET, "/games/${game.value}"), UriTemplate.from("/games/{gameId}")))
        response.status.successful shouldBe true
        val details = gameDetailsLens(response)
        details.won shouldBe true
    }

    fun guess(game: GameId, secret: String) {
        val response = client(
            RoutedRequest(Request(POST, "/games/${game.value}/guesses"), UriTemplate.from("/games/{gameId}/guesses"))
                .with(guessLens of Guess(secret))
        )
        response.status.successful shouldBe true
    }

    fun receivedHint(game: GameId, hint: String) {
        val response = client(RoutedRequest(Request(GET, "/games/${game.value}"), UriTemplate.from("/games/{gameId}")))
        response.status.successful shouldBe true
        val details = gameDetailsLens(response)
        details.hint shouldBe hint
    }

    fun checkLeaderboard(rankings: Map<String, Int>) {
        val response = client(Request(GET, "/leaderboard"))
        response.status.successful shouldBe true
        val leaderboard = leaderboardLens(response)
        leaderboard.rankings shouldContainAll rankings
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
    data class Leaderboard(val rankings: Map<String, Int>)
}
