package com.example.gameplay.infra

import com.example.gameplay.GameAlreadyCompleted
import com.example.gameplay.GameId
import com.example.gameplay.GameNotFound
import com.example.gameplay.SubmitGuess
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

object SubmitGuessApi {
    private val gameId = Path.value(GameId).of("id")
    private val submittedGuess = Body.auto<SubmittedGuess>().toLens()
    private val payload = Body.auto<GameUpdated>().toLens()

    operator fun invoke(submitGuess: SubmitGuess): RoutingHttpHandler {
        return "/games/{id}/guesses" bind POST to {
            submitGuess(gameId(it), submittedGuess(it).secret)
                .map { game ->
                    Response(CREATED).with(
                        payload of GameUpdated(
                        id = game.id.value,
                        hint = game.hint,
                        won = game.won
                    )
                    )
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

    data class SubmittedGuess(val secret: String)
    data class GameUpdated(val id: UUID, val hint: String, val won: Boolean)
}
