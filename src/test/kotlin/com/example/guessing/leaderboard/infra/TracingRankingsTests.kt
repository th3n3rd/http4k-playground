package com.example.guessing.leaderboard.infra

import com.example.guessing.common.infra.DatabaseCall
import com.example.guessing.leaderboard.Ranking
import com.example.guessing.leaderboard.Rankings
import com.example.guessing.leaderboard.RankingsContract
import com.example.guessing.player.EncodedPassword
import com.example.guessing.player.PlayerId
import com.example.guessing.player.RegisteredPlayer
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test

class TracingRankingsTests : RankingsContract {

    private val events = RecordingEvents()
    private val inMemoryRankings = Rankings.InMemory()

    private val alice = RegisteredPlayer(PlayerId(), "alice", EncodedPassword("alice"))
    private val bob = RegisteredPlayer(PlayerId(), "bob", EncodedPassword("bob"))
    private val charlie = RegisteredPlayer(PlayerId(), "charlie", EncodedPassword("charlie"))

    override val rankings = TracingRankings(events, inMemoryRankings)

    override fun given(ranking: Ranking): Ranking {
        inMemoryRankings.save(ranking)
        return ranking
    }

    override fun hasBeenStored()= Matcher<Ranking> { ranking ->
        MatcherResult(
            inMemoryRankings.findByPlayerId(ranking.playerId) != null,
            { "$ranking was not saved" },
            { "$ranking should not have been saved" },
        )
    }

    @Test
    fun `publish a new event when saving a ranking`() {
        val newRanking = Ranking(alice.id, alice.username, score = 100)

        rankings.save(newRanking)

        events.toList() shouldBe listOf(DatabaseCall("rankings", "save"))
    }

    @Test
    fun `publish a new event when retrieving an existing ranking by player id`() {
        val existingRanking = Ranking(alice.id, alice.username, score = 100)
        inMemoryRankings.save(existingRanking)

        rankings.findByPlayerId(existingRanking.playerId)

        events.toList() shouldBe listOf(DatabaseCall("rankings", "find by player id"))
    }

    @Test
    fun `publish a new event when retrieving an all existing rankings`() {
        val existingRankings = listOf(
            Ranking(alice.id, alice.username, score = 100),
            Ranking(bob.id, bob.username, score = 200),
            Ranking(charlie.id, charlie.username, score = 300),
        )
        existingRankings.forEach { inMemoryRankings.save(it) }

        rankings.findAll()

        events.toList() shouldBe listOf(DatabaseCall("rankings", "find all"))
    }
}