package com.example.player.infra

import com.example.common.infra.DatabaseCall
import com.example.player.PlayerId
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import org.http4k.events.Events
import org.http4k.filter.ZipkinTracesStorage
import org.http4k.filter.inChildSpan

class TracingRegisteredPlayers(private val events: Events, private val players: RegisteredPlayers) : RegisteredPlayers {
    override fun save(player: RegisteredPlayer) {
        trace("save")
        players.save(player)
    }

    override fun findByUsername(username: String): RegisteredPlayer? {
        trace("find by username")
        return players.findByUsername(username)
    }

    override fun existByUsername(username: String): Boolean {
        trace("exist by username")
        return players.existByUsername(username)
    }

    override fun findById(id: PlayerId): RegisteredPlayer?{
        trace("find by id")
        return players.findById(id)
    }

    private fun trace(operation: String) {
        ZipkinTracesStorage.THREAD_LOCAL.inChildSpan {
            events(DatabaseCall("players", operation))
        }
    }
}