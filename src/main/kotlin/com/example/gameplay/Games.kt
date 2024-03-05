package com.example.gameplay

interface Games {
    fun save(game: Game)
    fun findById(id: GameId): Game?

    companion object
}

