package com.example.player

interface Players {
    fun save(player: RegisteredPlayer)
    fun existsBy(username: String, password: EncodedPassword): Boolean
    fun findByUsername(username: String): RegisteredPlayer?
}
