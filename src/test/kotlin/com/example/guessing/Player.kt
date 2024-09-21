package com.example.guessing

import com.example.guessing.common.infra.ClientTracing
import com.example.guessing.common.infra.TracingEvents
import com.example.guessing.gameplay.GameId
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
    private var currentGame: GameId = GameId.Placeholder

    private val credentialsLens = Body.auto<Credentials>().toLens()
    private val gameStartedLens = Body.auto<GameStarted>().toLens()
    private val gameDetailsLens = Body.auto<GameDetails>().toLens()
    private val guessLens = Body.auto<Guess>().toLens()
    private val leaderboardLens = Body.auto<Leaderboard>().toLens()

    private val client = ClientTracing(TracingEvents(username, events, listOf("player")))
        .then(SetBaseUriFrom(baseUri))
        .then(BasicAuth(user = username, password = password))
        .then(JavaHttpClient())
        .debug()

    init {
        registers(username, password)
    }

    fun startsNewGame(): Player {
        val response = client(Request(POST, "/games"))
        response.status.successful shouldBe true
        currentGame = GameId(gameStartedLens(response).id)
        return this
    }

    fun confirmsHasWon(): Player {
        val response = client(RoutedRequest(Request(GET, "/games/${currentGame.value}"), UriTemplate.from("/games/{gameId}")))
        response.status.successful shouldBe true
        val details = gameDetailsLens(response)
        details.won shouldBe true
        return this
    }

    fun makesGuess(secret: String): Player {
        val response = client(
            RoutedRequest(Request(POST, "/games/${currentGame.value}/guesses"), UriTemplate.from("/games/{gameId}/guesses"))
                .with(guessLens of Guess(secret))
        )
        response.status.successful shouldBe true
        return this
    }

    fun confirmsReceivedHint(hint: String): Player {
        val response = client(RoutedRequest(Request(GET, "/games/${currentGame.value}"), UriTemplate.from("/games/{gameId}")))
        response.status.successful shouldBe true
        val details = gameDetailsLens(response)
        details.hint shouldBe hint
        return this
    }

    fun confirmsLeaderboardContains(rankings: Map<String, Int>): Player {
        val response = client(Request(GET, "/leaderboard"))
        response.status.successful shouldBe true
        val leaderboard = leaderboardLens(response)
        leaderboard.rankings shouldContainAll rankings
        return this
    }

    private fun registers(username: String, password: String): Player {
        val response = client(
            Request(POST, "/players")
                .with(credentialsLens of Credentials(username, password))
        )
        response.status.successful shouldBe true
        return this
    }

    data class Credentials(val username: String, val password: String)
    data class Guess(val secret: String)
    data class GameStarted(val id: UUID)
    data class GameDetails(val hint: String, val won: Boolean)
    data class Leaderboard(val rankings: Map<String, Int>)
}
