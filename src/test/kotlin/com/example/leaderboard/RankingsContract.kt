package com.example.leaderboard

import com.example.player.PlayerId
import io.kotest.matchers.Matcher
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

interface RankingsContract {
    val rankings: Rankings

    fun given(ranking: Ranking): Ranking
    fun hasBeenStored(): Matcher<Ranking>

    @Test
    fun `store new ranking`() {
        val newRanking = Ranking(PlayerId(), "alice", 100)

        rankings.save(newRanking)

        newRanking should hasBeenStored()
    }

    @Test
    fun `find stored ranking by player id`() {
        val existingRanking = given(Ranking(PlayerId(), "bob", 50))

        val foundRanking = rankings.findByPlayerId(existingRanking.playerId)

        foundRanking shouldBe existingRanking
    }

    @Test
    fun `find all stored rankings`() {
        val existingRankings = listOf(
            given(Ranking(PlayerId(), "bob", 50)),
            given(Ranking(PlayerId(), "bob", 50)),
        )

        val allFoundRankings = rankings.findAll()

        allFoundRankings shouldContainOnly existingRankings
    }
}