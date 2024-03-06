package com.example.player.infra

import com.example.common.infra.database.tables.references.PLAYERS
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import java.util.*
import org.jooq.DSLContext

fun RegisteredPlayers.Companion.Database(context: DSLContext) = DatabaseRegisteredPlayers(context)

class DatabaseRegisteredPlayers(private val context: DSLContext) : RegisteredPlayers {
    override fun save(player: RegisteredPlayer) {
        val playerRecord = context.newRecord(PLAYERS, player)
            .with(PLAYERS.ID, UUID.randomUUID())

        context
            .insertInto(PLAYERS)
            .set(playerRecord)
            .execute()
    }

    override fun findByUsername(username: String): RegisteredPlayer? {
        return context
            .select()
            .from(PLAYERS)
            .where(PLAYERS.USERNAME.eq(username))
            .fetchOne()
            ?.into(RegisteredPlayer::class.java)
    }

    override fun existByUsername(username: String): Boolean {
        return context.fetchExists(
            context.select()
                .from(PLAYERS)
                .where(PLAYERS.USERNAME.eq(username))
        )
    }
}
