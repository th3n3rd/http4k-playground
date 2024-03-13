package com.example.gameplay

import com.example.player.PlayerId

interface Games {
    fun save(game: Game)
    fun findById(id: GameId): Game?
    fun findByIdAndPlayerId(id: GameId, playerId: PlayerId): Game?

    companion object
}

