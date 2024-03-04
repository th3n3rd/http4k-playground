package com.example.gameplay

class StartNewGame(private val games: Games, private val secrets: Secrets) {
    operator fun invoke(): Game {
        val newGame = Game(secret = secrets.next())
        games.save(newGame)
        return newGame
    }
}
