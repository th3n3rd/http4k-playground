package com.example

import org.http4k.client.JavaHttpClient
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.format.Jackson.auto

class Player(baseUri: Uri) {

    private val client = SetBaseUriFrom(baseUri).then(JavaHttpClient())

    fun startNewGame(): GameId {
        val response = client(Request(POST, "/games"))
        return Body.auto<GameId>().toLens()(response)
    }

    fun hasWon(game: GameId): Boolean {
        val response = client(Request(GET, "/games/${game.value}"))
        val details = Body.auto<GameDetails>().toLens()(response)
        return details.won
    }
}
