package com.example.guessing.leaderboard.infra

import com.example.guessing.leaderboard.Rankings
import com.example.guessing.leaderboard.ShowLeaderboard
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.contract.security.NoSecurity
import org.http4k.contract.security.Security
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.http4k.format.Jackson.auto

object ShowLeaderboardApi {
    operator fun invoke(rankings: Rankings, authentication: Security): ContractRoute {
        return "/leaderboard" meta {
            security = authentication
            summary = "Show leaderboard"
            operationId = "showLeaderboard"

            returning(OK, Response.leaderboard to Leaderboard(mapOf("alice" to 100, "bob" to 50)),"Successful retrieval")
            returning(UNAUTHORIZED to "Not authenticated")
        } bindContract GET to { _ ->
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

fun ShowLeaderboard.asRoute(rankings: Rankings, authentication: Security = NoSecurity) =
    ShowLeaderboardApi(rankings, authentication)