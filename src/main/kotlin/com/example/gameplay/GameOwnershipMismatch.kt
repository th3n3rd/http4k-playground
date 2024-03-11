package com.example.gameplay

data class GameOwnershipMismatch(val gameId: GameId) : RuntimeException("Game with id $gameId is owned by a different player")
