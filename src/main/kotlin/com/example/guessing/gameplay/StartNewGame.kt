package com.example.guessing.gameplay

import com.example.architecture.Architecture
import com.example.guessing.common.infra.IdGenerator
import com.example.guessing.player.PlayerId

@Architecture.UseCase
class StartNewGame(
    private val games: Games,
    private val secrets: Secrets,
    private val idGenerator: IdGenerator
) {
    operator fun invoke(playerId: PlayerId): Game {
        val newGame = Game(id = GameId(idGenerator()), playerId = playerId, secret = secrets.next())
        games.save(newGame)
        return newGame
    }
}
