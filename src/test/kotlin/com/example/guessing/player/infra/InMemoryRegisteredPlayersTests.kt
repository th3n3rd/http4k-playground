package com.example.guessing.player.infra

import com.example.guessing.player.RegisteredPlayer
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.RegisteredPlayersContract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

class InMemoryRegisteredPlayersTests : RegisteredPlayersContract {

    override val players: RegisteredPlayers = RegisteredPlayers.InMemory()

    override fun given(player: RegisteredPlayer): RegisteredPlayer {
        players.save(player)
        return player
    }

    override fun haveBeenStored() = Matcher<RegisteredPlayer> { player ->
        MatcherResult(
            players.existByUsername(player.username),
            { "$player was not saved" },
            { "$player should not have been saved" },
        )
    }
}