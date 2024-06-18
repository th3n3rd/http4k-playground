package com.example.leaderboard.infra

import com.example.leaderboard.Rankings
import com.example.leaderboard.ShowLeaderboard
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto

object ShowLeaderboardApi {
    operator fun invoke(rankings: Rankings): ContractRoute {
        return "/leaderboard" bindContract GET to { _ ->
            Response(OK).with(
                Response.leaderboard of Leaderboard(
                    rankings = rankings
                        .findAll()
                        .associate { it.playerUsername to it.score }
                )
            )
        }
    }

    object Response {
        val leaderboard = Body.auto<Leaderboard>().toLens()
    }

    data class Leaderboard(val rankings: Map<String, Int>)
}

fun ShowLeaderboard.asRoute(rankings: Rankings) = ShowLeaderboardApi(rankings)