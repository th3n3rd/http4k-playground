package com.example.guessing.player.infra

import com.example.guessing.common.infra.DatabaseContext
import com.example.guessing.common.infra.RunDatabaseMigrations
import com.example.guessing.common.infra.TestEnvironment
import com.example.guessing.common.infra.database.tables.references.PLAYERS
import com.example.guessing.player.RegisteredPlayer
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.RegisteredPlayersContract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import org.junit.jupiter.api.BeforeEach

class DatabaseRegisteredPlayersTests : RegisteredPlayersContract {

    private val environment = TestEnvironment()
    private val context = DatabaseContext(environment)

    override val players = RegisteredPlayers.Database(context)

    @BeforeEach
    fun setUp() {
        RunDatabaseMigrations(environment)
    }

    override fun given(player: RegisteredPlayer): RegisteredPlayer {
        context
            .insertInto(PLAYERS, PLAYERS.ID, PLAYERS.USERNAME, PLAYERS.PASSWORD)
            .values(player.id.value, player.username, player.password.value)
            .execute()
        return player
    }

    override fun haveBeenStored() = Matcher<RegisteredPlayer> { player ->
        MatcherResult(
            context
                .fetchExists(
                    context.select()
                        .from(PLAYERS)
                        .where(PLAYERS.ID.eq(player.id.value))
                        .and(PLAYERS.USERNAME.eq(player.username))
                        .and(PLAYERS.PASSWORD.eq(player.password.value))
                ),
            { "$player was not persisted" },
            { "$player should not have been persisted" },
        )
    }
}

