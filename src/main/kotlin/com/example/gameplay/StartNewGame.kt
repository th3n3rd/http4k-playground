package com.example.gameplay

import com.example.player.PlayerId

class StartNewGame(private val games: Games, private val secrets: Secrets) {
    operator fun invoke(playerId: PlayerId): Game {
        val newGame = Game(playerId = playerId, secret = secrets.next(), guesses = listOf())
        games.save(newGame)
        return newGame
    }
}
