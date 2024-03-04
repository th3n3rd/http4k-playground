package com.example

interface Players {
    fun save(player: RegisteredPlayer)
    fun existsBy(username: String, password: EncodedPassword): Boolean
}
