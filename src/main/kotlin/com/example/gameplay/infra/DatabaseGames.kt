package com.example.gameplay.infra

import com.example.common.infra.database.tables.references.GAMES
import com.example.gameplay.Game
import com.example.gameplay.GameId
import com.example.gameplay.Games
import com.example.player.PlayerId
import org.jooq.DSLContext

fun Games.Companion.Database(database: DSLContext) = DatabaseGames(database)

class DatabaseGames(private val database: DSLContext) : Games {
    override fun save(game: Game) {
        val gameRecord = database.newRecord(GAMES, game)
        database
            .insertInto(GAMES)
            .set(gameRecord)
            .onDuplicateKeyUpdate()
            .set(gameRecord)
            .execute()
    }

    override fun findById(id: GameId): Game? {
        return database
            .select()
            .from(GAMES)
            .where(GAMES.ID.eq(id.value))
            .fetchOne()
            ?.into(Game::class.java)
    }

    override fun findByIdAndPlayerId(id: GameId, playerId: PlayerId): Game? {
        return database
            .select()
            .from(GAMES)
            .where(GAMES.ID.eq(id.value))
            .and(GAMES.PLAYER_ID.eq(playerId.value))
            .fetchOne()
            ?.into(Game::class.java)
    }

}
