package com.example

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Jackson.auto

class Player(val http: HttpHandler) {
    fun startNewGame(): GameId {
        val response = http(Request(Method.POST, "/games"))
        return Body.auto<GameId>().toLens()(response)
    }

    fun hasWon(game: GameId): Boolean {
        val response = http(Request(Method.GET, "/games/${game.value}"))
        val details = Body.auto<GameDetails>().toLens()(response)
        return details.won
    }
}
