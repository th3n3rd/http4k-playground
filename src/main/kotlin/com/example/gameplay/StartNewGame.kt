package com.example.gameplay

import com.example.common.infra.IdGenerator
import com.example.common.UseCase
import com.example.player.PlayerId

class StartNewGame(
    private val games: Games,
    private val secrets: Secrets,
    private val idGenerator: IdGenerator
) : UseCase {
    operator fun invoke(playerId: PlayerId): Game {
        val newGame = Game(id = GameId(idGenerator()), playerId = playerId, secret = secrets.next())
        games.save(newGame)
        return newGame
    }
}
