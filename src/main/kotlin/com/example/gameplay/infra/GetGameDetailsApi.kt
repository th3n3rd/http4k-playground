package com.example.gameplay.infra

import com.example.gameplay.GameId
import com.example.gameplay.Games
import com.example.gameplay.GetGameDetails
import com.example.player.PlayerId
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.security.NoSecurity
import org.http4k.contract.security.Security
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import java.util.*

object GetGameDetailsApi {

    operator fun invoke(
        games: Games,
        withPlayerId: RequestContextLens<PlayerId>,
        authentication: Security
    ): ContractRoute {
        return "/games" / Request.gameId meta {
            security = authentication
            summary = "Retrieve details of a specific game"
            returning(OK to "Successful retrieval of the game details")
            returning(NOT_FOUND to "Could not find the game")
        } bindContract GET to
            { gameId ->
                { req ->
                    games.findByIdAndPlayerId(gameId, withPlayerId(req))
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
    }

    data class GameDetails(val id: UUID, val hint: String, val won: Boolean)

    private object Request {
        val gameId = Path.map { GameId.parse(it) }.of("id")
    }

    private object Response {
        val gameDetails = Body.auto<GameDetails>().toLens()
    }
}

fun GetGameDetails.asRoute(withPlayerId: RequestContextLens<PlayerId>, authentication: Security = NoSecurity) =
    GetGameDetailsApi(games, withPlayerId, authentication)