package com.example.guessing.player

import com.example.architecture.Architecture
import com.example.guessing.common.infra.IdGenerator

@Architecture.UseCase
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
