package com.example.leaderboard.infra

import com.example.leaderboard.ShowLeaderboard
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object ShowLeaderboardApi {
    operator fun invoke(): RoutingHttpHandler {
        return "/leaderboard" bind Method.GET to {
            Response(OK).with(
                Response.leaderboard of Leaderboard(
                    rankings = mapOf(
                        "bob" to 100,
                        "charlie" to 50,
                        "alice" to 25
                    )
                )
            )
        }
    }

    object Response {
        val leaderboard = Body.auto<Leaderboard>().toLens()
    }

    data class Leaderboard(val rankings: Map<String, Int>)
}

fun ShowLeaderboard.asRoute() = ShowLeaderboardApi()