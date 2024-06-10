package com.example.leaderboard

import com.example.player.PlayerId

interface Rankings {
    fun save(ranking: Ranking)
    fun findAll(): List<Ranking>

    companion object
}

data class Ranking(val playerId: PlayerId, val playerUsername: String, val score: Int)