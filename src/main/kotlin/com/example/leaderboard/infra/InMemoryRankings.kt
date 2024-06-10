package com.example.leaderboard.infra

import com.example.leaderboard.Ranking
import com.example.leaderboard.Rankings
import java.util.concurrent.CopyOnWriteArrayList

fun Rankings.Companion.InMemory() = object: Rankings {
    private val rankings = CopyOnWriteArrayList<Ranking>()

    override fun save(ranking: Ranking) {
        rankings.add(ranking)
    }

    override fun findAll(): List<Ranking> {
        return rankings.toList()
    }
}

