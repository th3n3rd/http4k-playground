package com.example.player.infra

import com.example.player.Password
import com.example.player.PasswordEncoder
import com.example.player.PlayerId
import com.example.player.RegisteredPlayers
import org.http4k.contract.security.BasicAuthSecurity
import org.http4k.lens.RequestContextLens

object AuthenticatePlayer {
    operator fun invoke(
        players: RegisteredPlayers,
        passwordEncoder: PasswordEncoder,
        withPlayerId: RequestContextLens<PlayerId>
    ) = BasicAuthSecurity(
        realm = "example",
        key = withPlayerId,
        lookup = {
            players.findByUsername(it.user)
                ?.let { player ->
                    when {
                        passwordEncoder(Password(it.password), player.password) -> player.id
                        else -> null
                    }
                }
        })

}
