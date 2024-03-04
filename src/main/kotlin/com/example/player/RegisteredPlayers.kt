package com.example.player

interface RegisteredPlayers {
    fun save(player: RegisteredPlayer)
    fun findByUsername(username: String): RegisteredPlayer?
}
