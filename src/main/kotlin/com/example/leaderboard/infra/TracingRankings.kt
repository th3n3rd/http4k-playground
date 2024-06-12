package com.example.leaderboard.infra

import com.example.common.infra.DatabaseCall
import com.example.leaderboard.Ranking
import com.example.leaderboard.Rankings
import com.example.player.PlayerId
import org.http4k.events.Events
import org.http4k.filter.ZipkinTracesStorage
import org.http4k.filter.inChildSpan

class TracingRankings(val events: Events, val rankings: Rankings) : Rankings {
    override fun save(ranking: Ranking) {
        trace("save")
        rankings.save(ranking)
    }

    override fun findAll(): List<Ranking> {
        trace("find all")
        return rankings.findAll()
    }

    override fun findByPlayerId(playerId: PlayerId): Ranking? {
        trace("find by player id")
        return rankings.findByPlayerId(playerId)
    }

    private fun trace(operation: String) {
        ZipkinTracesStorage.THREAD_LOCAL.inChildSpan {
            events(DatabaseCall("rankings", operation))
        }
    }
}