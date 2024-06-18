package com.example

import com.example.common.infra.EventsBus
import com.example.gameplay.*
import com.example.gameplay.infra.asRoute
import com.example.leaderboard.Rankings
import com.example.leaderboard.ShowLeaderboard
import com.example.leaderboard.TrackPerformances
import com.example.leaderboard.infra.asRoute
import com.example.leaderboard.infra.asTask
import com.example.player.PasswordEncoder
import com.example.player.RegisterNewPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.AuthenticatePlayer
import com.example.player.infra.PlayerRequestContext
import com.example.player.infra.PlayerRequestContext.withPlayerId
import com.example.player.infra.asRoute
import com.example.player.infra.protectedBy
import org.http4k.contract.contract
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.routing.routes

object GuessTheSecretApp {
    operator fun invoke(
        eventsBus: EventsBus,
        players: RegisteredPlayers,
        games: Games,
        secrets: Secrets,
        rankings: Rankings,
        passwordEncoder: PasswordEncoder
    ): HttpHandler {
        val authentication = AuthenticatePlayer(players, passwordEncoder, withPlayerId)

        TrackPerformances(players, rankings)
            .asTask(eventsBus)

        return PlayerRequestContext()
            .then(
                routes(
                    contract {
                        routes += RegisterNewPlayer(players, passwordEncoder)
                            .asRoute()
                    },
                    StartNewGame(games, secrets)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    GetGameDetails(games)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    SubmitGuess(games, eventsBus)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    ShowLeaderboard()
                        .asRoute(rankings)
                )
            )
    }
}