package com.example

import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import java.util.*
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
import org.http4k.lens.value
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

fun SubmitGuessApi(submitGuess: SubmitGuess): RoutingHttpHandler {
    data class SubmittedGuess(val secret: String)
    data class GameUpdated(val id: UUID, val hint: String, val won: Boolean)

    val gameId = Path.value(GameId).of("id")
    val submittedGuess = Body.auto<SubmittedGuess>().toLens()
    val payload = Body.auto<GameUpdated>().toLens()

    return "/games/{id}/guesses" bind POST to {
        submitGuess(gameId(it), submittedGuess(it).secret)
            .map { game ->
                Response(CREATED).with(payload of GameUpdated(
                    id = game.id.value,
                    hint = game.hint,
                    won = game.won
                ))
            }
            .mapFailure { error ->
                when (error) {
                    is GameNotFound -> Response(NOT_FOUND)
                    is GameAlreadyCompleted -> Response(BAD_REQUEST)
                    else -> Response(INTERNAL_SERVER_ERROR)
                }
            }
            .get()
    }
}
