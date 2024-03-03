package com.example

class RegisterNewPlayer(private val players: Players) {

    data class Command(val username: String, val password: String)

    operator fun invoke(command: Command) {
        players.save(RegisteredPlayer(
            command.username,
            command.password
        ))
    }
}
