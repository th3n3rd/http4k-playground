package com.example.player.infra

import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import java.util.concurrent.CopyOnWriteArrayList

fun RegisteredPlayers.Companion.InMemory() = InMemoryRegisteredPlayers()

class InMemoryRegisteredPlayers : RegisteredPlayers {
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
