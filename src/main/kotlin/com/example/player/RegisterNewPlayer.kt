package com.example.player

class RegisterNewPlayer(private val players: RegisteredPlayers, private val passwordEncoder: PasswordEncoder) {

    data class Command(val username: String, val password: String)

    operator fun invoke(command: Command) {
        players.save(
            RegisteredPlayer(
                username = command.username,
                password = passwordEncoder(Password(command.password))
            )
        )
    }
}
