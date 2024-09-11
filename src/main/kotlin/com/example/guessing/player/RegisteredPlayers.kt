package com.example.guessing.player

interface RegisteredPlayers {
    fun save(player: RegisteredPlayer)
    fun findByUsername(username: String): RegisteredPlayer?
    fun existByUsername(username: String): Boolean
    fun findById(id: PlayerId): RegisteredPlayer?

    companion object
}
