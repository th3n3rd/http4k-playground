package com.example.player

import java.util.concurrent.CopyOnWriteArrayList

class InMemoryPlayers : Players {
    private val players = CopyOnWriteArrayList<RegisteredPlayer>()

    override fun save(player: RegisteredPlayer) {
        players.add(player)
    }

    override fun findByUsername(username: String): RegisteredPlayer? {
        return players.find { it.username == username }
    }

    fun findAll(): List<RegisteredPlayer> {
        return players
    }
}
