package com.example.gameplay.infra

import com.example.common.infra.AppRequestContext
import com.example.gameplay.GameId
import com.example.gameplay.Games
import com.example.player.PlayerId
import java.util.*
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

object GetGameDetailsApi {
    private val gameIdLens = Path.map { GameId.parse(it) }.of("id")
    private val gameDetailsLens = Body.auto<GameDetails>().toLens()

    operator fun invoke(games: Games, authenticatedPlayerIdLens: RequestContextLens<PlayerId>): RoutingHttpHandler {
        return "/games/{id}" bind GET to {
            games.findByIdAndPlayerId(gameIdLens(it), authenticatedPlayerIdLens(it))
                ?.let { currentGame ->
                    Response(OK).with(
                        gameDetailsLens of GameDetails(
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
