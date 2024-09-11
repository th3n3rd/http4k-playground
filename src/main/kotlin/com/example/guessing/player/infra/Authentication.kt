package com.example.guessing.player.infra

import com.example.guessing.player.Password
import com.example.guessing.player.PasswordEncoder
import com.example.guessing.player.PlayerId
import com.example.guessing.player.RegisteredPlayers
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
