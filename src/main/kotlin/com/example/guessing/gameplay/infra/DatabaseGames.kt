package com.example.guessing.gameplay.infra

import com.example.guessing.common.infra.database.tables.references.GAMES
import com.example.guessing.common.infra.database.tables.references.GAME_GUESSES
import com.example.guessing.gameplay.Game
import com.example.guessing.gameplay.GameId
import com.example.guessing.gameplay.Games
import com.example.guessing.player.PlayerId
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select

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

        database
            .deleteFrom(GAME_GUESSES)
            .where(GAME_GUESSES.GAME_ID.eq(game.id.value))
            .execute()

        game.guesses.forEach {
            val guessRecord = database
                .newRecord(GAME_GUESSES, it)
                .with(GAME_GUESSES.GAME_ID, game.id.value)
            database.insertInto(GAME_GUESSES)
                .set(guessRecord)
                .execute()
        }
    }

    override fun findByIdAndPlayerId(id: GameId, playerId: PlayerId): Game? {
        return database
            .select(
                GAMES.ID,
                GAMES.PLAYER_ID,
                GAMES.SECRET,
                multiset(
                    select(GAME_GUESSES.SECRET)
                        .from(GAME_GUESSES)
                        .where(GAME_GUESSES.GAME_ID.eq(GAMES.ID))
                ).`as`("guesses").convertFrom { it.into(Game.Guess::class.java) }
            )
            .from(GAMES)
            .where(GAMES.ID.eq(id.value))
            .and(GAMES.PLAYER_ID.eq(playerId.value))
            .fetchOne()
            ?.into(Game::class.java)
    }

}
