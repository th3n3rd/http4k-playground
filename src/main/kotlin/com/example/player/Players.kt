package com.example.player

interface Players {
    fun save(player: RegisteredPlayer)
    fun findByUsername(username: String): RegisteredPlayer?
}
