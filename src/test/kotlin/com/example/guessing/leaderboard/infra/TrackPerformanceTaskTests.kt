package com.example.guessing.leaderboard.infra

import com.example.guessing.common.infra.EventsBus
import com.example.guessing.common.infra.InMemory
import com.example.guessing.gameplay.GameCompleted
import com.example.guessing.gameplay.GameId
import com.example.guessing.leaderboard.Ranking
import com.example.guessing.leaderboard.Rankings
import com.example.guessing.leaderboard.infra.InMemory
import com.example.guessing.leaderboard.infra.asTask
import com.example.guessing.leaderboard.TrackPerformances
import com.example.guessing.player.EncodedPassword
import com.example.guessing.player.PlayerId
import com.example.guessing.player.RegisteredPlayer
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.infra.InMemory
import io.kotest.matchers.collections.shouldContainOnly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrackPerformanceTaskTests {

    private val eventsBus = EventsBus.InMemory()
    private val rankings = Rankings.InMemory()
    private val players = RegisteredPlayers.InMemory()
    private val task = TrackPerformances(
        players = players,
        rankings = rankings
    ).asTask(eventsBus)

    private val alice = RegisteredPlayer(PlayerId(), "alice", EncodedPassword("alice"))
    private val bob = RegisteredPlayer(PlayerId(), "bob", EncodedPassword("bob"))

    @BeforeEach
    fun setUp() {
        players.save(alice)
        players.save(bob)
    }

    @Test
    fun `record a player performance when games are completed`() {
        eventsBus(GameCompleted(GameId(), alice.id, attempts = 1))

        rankings.findAll() shouldContainOnly listOf(
            Ranking(alice.id, alice.username, 100)
        )
    }

    @Test
    fun `record a player performances across multiple completed games`() {
        eventsBus(GameCompleted(GameId(), alice.id, attempts = 1))
        eventsBus(GameCompleted(GameId(), alice.id, attempts = 1))

        rankings.findAll() shouldContainOnly listOf(
            Ranking(alice.id, alice.username, 200)
        )
    }

    @Test
    fun `record multiple players performances`() {
        eventsBus(GameCompleted(GameId(), alice.id, attempts = 1))
        eventsBus(GameCompleted(GameId(), bob.id, attempts = 1))

        rankings.findAll() shouldContainOnly listOf(
            Ranking(alice.id, alice.username, 100),
            Ranking(bob.id, bob.username, 100),
        )
    }
}