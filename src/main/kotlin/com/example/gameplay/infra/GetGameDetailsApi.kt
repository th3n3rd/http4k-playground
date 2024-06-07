package com.example.gameplay.infra

import com.example.gameplay.GameId
import com.example.gameplay.Games
import com.example.gameplay.GetGameDetails
import com.example.player.PlayerId
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.util.*

object GetGameDetailsApi {

    operator fun invoke(games: Games, authenticatedPlayerIdLens: RequestContextLens<PlayerId>): RoutingHttpHandler {
        return "/games/{id}" bind GET to {
            games.findByIdAndPlayerId(Request.gameId(it), authenticatedPlayerIdLens(it))
                ?.let { currentGame ->
                    Response(OK).with(
                        Response.gameDetails of GameDetails(
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

    private object Request {
        val gameId = Path.map { GameId.parse(it) }.of("id")
    }

    private object Response {
        val gameDetails = Body.auto<GameDetails>().toLens()
    }
}

fun GetGameDetails.asRoute(authenticatedPlayerIdLens: RequestContextLens<PlayerId>) = GetGameDetailsApi(games, authenticatedPlayerIdLens)