package com.example

import java.util.*
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind

private data class GameDetails(val id: UUID, val hint: String, val won: Boolean)
private val gameId = Path.value(GameId).of("id")
private val payload = Body.auto<GameDetails>().toLens()

fun GetGameDetailsApi(games: Games = InMemoryGames()) = "/games/{id}" bind Method.GET to {
    val currentGame = games.findById(gameId(it))!!
    Response(Status.OK).with(
        payload of GameDetails(
            id = currentGame.id.value,
            hint = currentGame.hint,
            won = currentGame.won
        )
    )
}
