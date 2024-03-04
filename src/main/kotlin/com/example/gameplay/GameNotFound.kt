package com.example.gameplay

data class GameNotFound(val gameId: GameId) : RuntimeException("Game with id ${gameId.value} not found")
