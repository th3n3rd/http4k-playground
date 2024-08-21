package com.example.player

import com.example.common.infra.IdGenerator

class RegisterNewPlayer(
    private val players: RegisteredPlayers,
    private val passwordEncoder: PasswordEncoder,
    private val idGenerator: IdGenerator
) {

    data class Command(val username: String, val password: String)

    operator fun invoke(command: Command) {
        players.save(
            RegisteredPlayer(
                id = PlayerId(idGenerator()),
                username = command.username,
                password = passwordEncoder(Password(command.password))
            )
        )
    }
}
