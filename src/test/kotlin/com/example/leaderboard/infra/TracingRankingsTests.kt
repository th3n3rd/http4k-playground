package com.example.leaderboard.infra

import com.example.common.infra.DatabaseCall
import com.example.leaderboard.Ranking
import com.example.leaderboard.Rankings
import com.example.player.EncodedPassword
import com.example.player.PlayerId
import com.example.player.RegisteredPlayer
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test

class TracingRankingsTests {

    private val events = RecordingEvents()
    private val rankings = Rankings.InMemory()
    private val tracingRankings = TracingRankings(events, rankings)

    private val alice = RegisteredPlayer(PlayerId(), "alice", EncodedPassword("alice"))
    private val bob = RegisteredPlayer(PlayerId(), "bob", EncodedPassword("bob"))
    private val charlie = RegisteredPlayer(PlayerId(), "charlie", EncodedPassword("charlie"))

    @Test
    fun `publish a new event when saving a ranking`() {
        val newRanking = Ranking(alice.id, alice.username, score = 100)

        tracingRankings.save(newRanking)

        rankings.findByPlayerId(alice.id) shouldNot beNull()
        events.toList() shouldBe listOf(DatabaseCall("rankings", "save"))
    }

    @Test
    fun `publish a new event when retrieving an existing ranking by player id`() {
        val existingRanking = Ranking(alice.id, alice.username, score = 100)
        rankings.save(existingRanking)

        val retrievedRanking = tracingRankings.findByPlayerId(existingRanking.playerId)

        retrievedRanking shouldBe existingRanking
        events.toList() shouldBe listOf(DatabaseCall("rankings", "find by player id"))
    }

    @Test
    fun `publish a new event when retrieving an all existing rankings`() {
        val existingRankings = listOf(
            Ranking(alice.id, alice.username, score = 100),
            Ranking(bob.id, bob.username, score = 200),
            Ranking(charlie.id, charlie.username, score = 300),
        )
        existingRankings.forEach { rankings.save(it) }

        val retrievedRankings = tracingRankings.findAll()

        retrievedRankings shouldContainOnly existingRankings
        events.toList() shouldBe listOf(DatabaseCall("rankings", "find all"))
    }
}