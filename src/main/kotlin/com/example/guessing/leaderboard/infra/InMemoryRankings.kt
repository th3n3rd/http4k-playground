package com.example.guessing.leaderboard.infra

import com.example.guessing.leaderboard.Ranking
import com.example.guessing.leaderboard.Rankings
import com.example.guessing.player.PlayerId
import java.util.concurrent.ConcurrentHashMap

fun Rankings.Companion.InMemory() = object: Rankings {
    private val rankingsByPlayer = ConcurrentHashMap<PlayerId, Ranking>()

    override fun save(ranking: Ranking) {
        rankingsByPlayer[ranking.playerId] = ranking
    }

    override fun findAll(): List<Ranking> {
        return rankingsByPlayer.values.toList()
    }

    override fun findByPlayerId(playerId: PlayerId): Ranking? {
        return rankingsByPlayer[playerId]
    }
}

