package com.example

class StartNewGame(private val games: Games) {
    operator fun invoke(): Game {
        val newGame = Game()
        games.save(newGame)
        return newGame
    }
}
