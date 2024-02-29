package com.example

fun StartNewGame(games: Games): Game {
    val newGame = Game()
    games.save(newGame)
    return newGame
}
