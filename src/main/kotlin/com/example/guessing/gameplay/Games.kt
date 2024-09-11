package com.example.guessing.gameplay

import com.example.guessing.player.PlayerId

interface Games {
    fun save(game: Game)
    fun findByIdAndPlayerId(id: GameId, playerId: PlayerId): Game?

    companion object
}

