package com.example.leaderboard

import com.example.leaderboard.infra.InMemory
import com.example.player.EncodedPassword
import com.example.player.PlayerId
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import com.example.player.infra.InMemory
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.should
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrackPerformancesTests {
    private val players = RegisteredPlayers.InMemory()
    private val rankings = Rankings.InMemory()
    private val trackPerformances = TrackPerformances(players, rankings)

    private val alice = newPlayer("alice")
    private val bob = newPlayer("bob")
    private val charlie = newPlayer("charlie")
    private val dave = newPlayer("dave")

    @BeforeEach
    fun setUp() {
        players.save(alice)
        players.save(bob)
        players.save(charlie)
        players.save(dave)
    }

    @Test
    fun `store a new ranking if does not exist for the player`() {
        val result = trackPerformances(playerId = alice.id, attempts = 1)

        result shouldBeSuccess Unit
        rankings.findAll() shouldContainOnly listOf(
            Ranking(alice.id, alice.username, 100)
        )
    }

    @Test
    fun `sum up the score if the ranking already exist for the player`() {
        trackPerformances(playerId = alice.id, attempts = 1)
        val result = trackPerformances(playerId = alice.id, attempts = 1)

        result shouldBeSuccess Unit
        rankings.findAll() shouldContainOnly listOf(
            Ranking(alice.id, alice.username, 200)
        )
    }

    @Test
    fun `track multiple players`() {
        trackPerformances(playerId = alice.id, attempts = 1)
        trackPerformances(playerId = bob.id, attempts = 1)

        rankings.findAll() shouldContainOnly listOf(
            Ranking(alice.id, alice.username, 100),
            Ranking(bob.id, bob.username, 100),
        )
    }

    @Test
    fun `the score assigned starts with 100 and decrease proportionally to the number of attempts`() {
        trackPerformances(playerId = alice.id, attempts = 1)
        trackPerformances(playerId = bob.id, attempts = 2)
        trackPerformances(playerId = charlie.id, attempts = 3)
        trackPerformances(playerId = dave.id, attempts = 4)

        rankings should haveRankings(mapOf(
            alice.username to 100,
            bob.username to 50,
            charlie.username to 33,
            dave.username to 25,
        ))
    }

    @Test
    fun `fails when ranking a player that does not exist`() {
        val nonExistingPlayer = PlayerId()

        val result = trackPerformances(nonExistingPlayer, attempts = 1)

        result shouldBeFailure TrackPerformancesError.PlayerNotFound(nonExistingPlayer)
    }

    private fun newPlayer(username: String) = RegisteredPlayer(
        id = PlayerId(),
        username = username,
        password = EncodedPassword(username)
    )

    private fun haveRankings(expected: Map<String, Int>) = Matcher<Rankings> { rankings ->
        val actual = rankings.findAll().associate { it.playerUsername to it.score }

        MatcherResult(
            actual == expected,
            { "Expected rankings $actual to be $expected" },
            { "Expected rankings $actual not to be $expected" },
        )
    }
}

