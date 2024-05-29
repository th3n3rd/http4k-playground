package com.example.player.infra

import com.example.common.infra.DatabaseCall
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import org.http4k.events.Events

class TracingRegisteredPlayers(private val events: Events, private val players: RegisteredPlayers) : RegisteredPlayers {
    override fun save(player: RegisteredPlayer) {
        events(DatabaseCall("players", "save"))
        players.save(player)
    }

    override fun findByUsername(username: String): RegisteredPlayer? {
        events(DatabaseCall("players", "find by username"))
        return players.findByUsername(username)
    }

    override fun existByUsername(username: String): Boolean {
        events(DatabaseCall("players", "exist by username"))
        return players.existByUsername(username)
    }

}