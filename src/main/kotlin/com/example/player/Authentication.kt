package com.example.player

import org.http4k.filter.ServerFilters

fun AuthenticatePlayer(players: RegisteredPlayers, passwordEncoder: PasswordEncoder) =
    ServerFilters.BasicAuth("example") {
        players.findByUsername(it.user)
            ?.let { player -> passwordEncoder(Password(it.password), player.password) }
            ?: false
    }
