package com.example.gameplay

data class GameAlreadyCompleted(val gameId: GameId) : RuntimeException("Game with id ${gameId.value} is already completed")
