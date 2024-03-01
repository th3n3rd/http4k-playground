package com.example

data class GameNotFound(val gameId: GameId) : RuntimeException("Game with id ${gameId.value} not found")
