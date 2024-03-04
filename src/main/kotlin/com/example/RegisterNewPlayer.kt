package com.example

class RegisterNewPlayer(private val players: Players, private val passwordEncoder: PasswordEncoder) {

    data class Command(val username: String, val password: String)

    operator fun invoke(command: Command) {
        players.save(RegisteredPlayer(
            command.username,
            passwordEncoder(Password(command.password))
        ))
    }
}
