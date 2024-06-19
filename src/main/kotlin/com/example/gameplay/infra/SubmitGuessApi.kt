package com.example.gameplay.infra

import com.example.gameplay.GameId
import com.example.gameplay.SubmitGuess
import com.example.gameplay.SubmitGuessError.CouldNotGuess
import com.example.gameplay.SubmitGuessError.GameNotFound
import com.example.player.PlayerId
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import java.util.*

object SubmitGuessApi {

    operator fun invoke(submitGuess: SubmitGuess, withPlayerId: RequestContextLens<PlayerId>): ContractRoute {
        return "/games" / Request.gameId / "guesses" bindContract POST to { gameId, _ ->
            { req ->
                submitGuess(gameId, Request.submittedGuess(req).secret, withPlayerId(req))
                    .map { game ->
                        Response(CREATED).with(
                            Response.gameUpdated of GameUpdated(
                                id = game.id.value,
                                playerId = game.playerId.value,
                                hint = game.hint,
                                won = game.won
                            )
                        )
                    }
                    .recover { error ->
                        when (error) {
                            is GameNotFound -> Response(NOT_FOUND)
                            is CouldNotGuess -> Response(BAD_REQUEST)
                            else -> Response(INTERNAL_SERVER_ERROR)
                        }
                    }
            }
        }
    }

    data class SubmittedGuess(val secret: String)
    data class GameUpdated(val id: UUID, val playerId: UUID, val hint: String, val won: Boolean)

    private object Request {
        val gameId = Path.map { GameId.parse(it) }.of("id")
        val submittedGuess = Body.auto<SubmittedGuess>().toLens()
    }

    private object Response {
        val gameUpdated = Body.auto<GameUpdated>().toLens()
    }
}

fun SubmitGuess.asRoute(withPlayerId: RequestContextLens<PlayerId>) = SubmitGuessApi(this, withPlayerId)
