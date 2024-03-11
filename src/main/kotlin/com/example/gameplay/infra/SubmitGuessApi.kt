package com.example.gameplay.infra

import com.example.gameplay.GameAlreadyCompleted
import com.example.gameplay.GameId
import com.example.gameplay.GameNotFound
import com.example.gameplay.GameOwnershipMismatch
import com.example.gameplay.SubmitGuess
import com.example.player.PlayerId
import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import java.util.*
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object SubmitGuessApi {
    private val gameIdLens = Path.map { GameId.parse(it) }.of("id")
    private val submittedGuessLens = Body.auto<SubmittedGuess>().toLens()
    private val gameUpdatedLens = Body.auto<GameUpdated>().toLens()

    operator fun invoke(submitGuess: SubmitGuess, authenticatedPlayerIdLens: RequestContextLens<PlayerId>): RoutingHttpHandler {
        return "/games/{id}/guesses" bind POST to {
            submitGuess(gameIdLens(it), submittedGuessLens(it).secret, authenticatedPlayerIdLens(it))
                .map { game ->
                    Response(CREATED).with(
                        gameUpdatedLens of GameUpdated(
                            id = game.id.value,
                            playerId = game.playerId.value,
                            hint = game.hint,
                            won = game.won
                        )
                    )
                }
                .mapFailure { error ->
                    when (error) {
                        is GameNotFound -> Response(NOT_FOUND)
                        is GameAlreadyCompleted -> Response(BAD_REQUEST)
                        is GameOwnershipMismatch -> Response(FORBIDDEN)
                        else -> Response(INTERNAL_SERVER_ERROR)
                    }
                }
                .get()
        }
    }

    data class SubmittedGuess(val secret: String)
    data class GameUpdated(val id: UUID, val playerId: UUID, val hint: String, val won: Boolean)
}
