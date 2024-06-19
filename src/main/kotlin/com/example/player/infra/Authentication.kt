package com.example.player.infra

import com.example.player.Password
import com.example.player.PasswordEncoder
import com.example.player.PlayerId
import com.example.player.RegisteredPlayers
import org.http4k.contract.security.BasicAuthSecurity
import org.http4k.contract.security.Security
import org.http4k.core.Filter
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BasicAuth
import org.http4k.lens.RequestContextLens
import org.http4k.routing.RoutingHttpHandler

object AuthenticatePlayer {
    operator fun invoke(
        players: RegisteredPlayers,
        passwordEncoder: PasswordEncoder,
        withPlayerId: RequestContextLens<PlayerId>
    ) =
        BasicAuth("example", withPlayerId) {
            players.findByUsername(it.user)
                ?.let { player ->
                    when {
                        passwordEncoder(Password(it.password), player.password) -> player.id
                        else -> null
                    }
                }
        }

    fun asSecurity(
        players: RegisteredPlayers,
        passwordEncoder: PasswordEncoder,
        withPlayerId: RequestContextLens<PlayerId>
    ): Security =
        BasicAuthSecurity(
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

fun RoutingHttpHandler.protectedBy(authentication: Filter) = authentication.then(this)