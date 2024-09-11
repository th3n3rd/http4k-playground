package com.example.guessing.player.infra

import com.example.guessing.player.PlayerId
import com.example.guessing.player.RegisteredPlayer
import com.example.guessing.player.RegisteredPlayers
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

    override fun existByUsername(username: String): Boolean {
        return players.any { it.username == username }
    }

    override fun findById(id: PlayerId): RegisteredPlayer? {
        return players.find { it.id == id }
    }

    fun findAll(): List<RegisteredPlayer> {
        return players
    }
}
