package com.example.player.infra

import com.example.common.infra.database.tables.references.PLAYERS
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import java.util.*
import org.jooq.DSLContext

fun RegisteredPlayers.Companion.Database(database: DSLContext) = DatabaseRegisteredPlayers(database)

class DatabaseRegisteredPlayers(private val database: DSLContext) : RegisteredPlayers {
    override fun save(player: RegisteredPlayer) {
        val playerRecord = database.newRecord(PLAYERS, player)

        database
            .insertInto(PLAYERS)
            .set(playerRecord)
            .execute()
    }

    override fun findByUsername(username: String): RegisteredPlayer? {
        return database
            .select()
            .from(PLAYERS)
            .where(PLAYERS.USERNAME.eq(username))
            .fetchOne()
            ?.into(RegisteredPlayer::class.java)
    }

    override fun existByUsername(username: String): Boolean {
        return database.fetchExists(
            database.select()
                .from(PLAYERS)
                .where(PLAYERS.USERNAME.eq(username))
        )
    }
}
