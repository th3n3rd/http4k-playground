package com.example.player.infra

import com.example.player.Password
import com.example.player.PasswordEncoder
import com.example.player.PlayerId
import com.example.player.RegisteredPlayers
import org.http4k.filter.ServerFilters.BasicAuth
import org.http4k.lens.RequestContextLens

object AuthenticatePlayer {
    operator fun invoke(players: RegisteredPlayers, passwordEncoder: PasswordEncoder, authenticatedPlayerId: RequestContextLens<PlayerId>) =
        BasicAuth("example", authenticatedPlayerId) {
            players.findByUsername(it.user)
                ?.let { player ->
                    when {
                        passwordEncoder(Password(it.password), player.password) -> player.id
                        else -> null
                    }
                }
        }
}
