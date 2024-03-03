package com.example

interface Players {
    fun save(player: RegisteredPlayer)
    fun existsBy(username: String, encodedPassword: String): Boolean
}
