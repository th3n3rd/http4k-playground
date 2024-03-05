package com.example.gameplay

import java.util.*
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object GetGameDetailsApi {
    private val gameId = Path.value(GameId).of("id")
    private val payload = Body.auto<GameDetails>().toLens()

    operator fun invoke(games: Games): RoutingHttpHandler {
        return "/games/{id}" bind GET to {
            games.findById(gameId(it))
                ?.let { currentGame ->
                    Response(OK).with(
                        payload of GameDetails(
                            id = currentGame.id.value,
                            hint = currentGame.hint,
                            won = currentGame.won
                        )
                    )
                }
                ?: Response(NOT_FOUND)
        }
    }

    data class GameDetails(val id: UUID, val hint: String, val won: Boolean)
}
