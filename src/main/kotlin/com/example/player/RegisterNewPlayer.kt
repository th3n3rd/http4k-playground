package com.example.player

class RegisterNewPlayer(private val players: RegisteredPlayers, private val passwordEncoder: PasswordEncoder) {

    data class Command(val username: String, val password: String)

    operator fun invoke(command: Command) {
        players.save(
            RegisteredPlayer(
                command.username,
                passwordEncoder(Password(command.password))
            )
        )
    }
}
