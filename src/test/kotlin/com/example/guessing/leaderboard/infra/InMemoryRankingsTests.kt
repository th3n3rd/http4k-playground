package com.example.guessing.leaderboard.infra

import com.example.guessing.leaderboard.Ranking
import com.example.guessing.leaderboard.Rankings
import com.example.guessing.leaderboard.RankingsContract
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

class InMemoryRankingsTests : RankingsContract {

    override val rankings = Rankings.InMemory()

    override fun given(ranking: Ranking): Ranking {
        rankings.save(ranking)
        return ranking
    }

    override fun hasBeenStored() = Matcher<Ranking> { ranking ->
        MatcherResult(
            rankings.findAll().contains(ranking),
            { "$ranking was not saved" },
            { "$ranking should not have been saved" },
        )
    }
}