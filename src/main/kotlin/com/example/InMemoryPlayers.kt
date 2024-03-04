package com.example

import java.util.concurrent.CopyOnWriteArrayList

class InMemoryPlayers : Players {
    private val players = CopyOnWriteArrayList<RegisteredPlayer>()

    override fun save(player: RegisteredPlayer) {
        players.add(player)
    }

    override fun existsBy(username: String, password: EncodedPassword): Boolean {
        return players.any { it.username == username && it.password == password}
    }

    override fun findByUsername(username: String): RegisteredPlayer? {
        return players.find { it.username == username }
    }

}
