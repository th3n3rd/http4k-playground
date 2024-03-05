package com.example.player.infra

import com.example.player.Password
import com.example.player.PasswordEncoder
import com.example.player.RegisteredPlayers
import org.http4k.filter.ServerFilters.BasicAuth

object AuthenticatePlayer {
    operator fun invoke(players: RegisteredPlayers, passwordEncoder: PasswordEncoder) =
        BasicAuth("example") {
            players.findByUsername(it.user)
                ?.let { player -> passwordEncoder(Password(it.password), player.password) }
                ?: false
        }
}
