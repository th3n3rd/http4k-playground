package com.example.guessing.player

import com.example.guessing.common.UseCase
import com.example.guessing.common.infra.IdGenerator

class RegisterNewPlayer(
    private val players: RegisteredPlayers,
    private val passwordEncoder: PasswordEncoder,
    private val idGenerator: IdGenerator
) : UseCase {
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
