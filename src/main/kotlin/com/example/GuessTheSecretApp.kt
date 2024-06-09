package com.example

import com.example.common.infra.AppRequestContext
import com.example.common.infra.AppRequestContext.withPlayerId
import com.example.gameplay.*
import com.example.gameplay.infra.asRoute
import com.example.leaderboard.infra.ShowLeaderboard
import com.example.leaderboard.infra.asRoute
import com.example.player.PasswordEncoder
import com.example.player.RegisterNewPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.AuthenticatePlayer
import com.example.player.infra.asRoute
import com.example.player.infra.protectedBy
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.routing.routes

object GuessTheSecretApp {
    operator fun invoke(
        players: RegisteredPlayers,
        games: Games,
        secrets: Secrets,
        passwordEncoder: PasswordEncoder
    ): HttpHandler {
        val authentication = AuthenticatePlayer(players, passwordEncoder, withPlayerId)
        return AppRequestContext()
            .then(
                routes(
                    RegisterNewPlayer(players, passwordEncoder)
                        .asRoute(),
                    StartNewGame(games, secrets)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    GetGameDetails(games)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    SubmitGuess(games)
                        .asRoute(withPlayerId)
                        .protectedBy(authentication),
                    ShowLeaderboard()
                        .asRoute()
                )
            )
    }
}