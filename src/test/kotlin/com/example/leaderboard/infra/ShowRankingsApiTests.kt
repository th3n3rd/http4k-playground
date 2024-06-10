package com.example.leaderboard.infra

import com.example.common.infra.authenticatedAs
import com.example.leaderboard.Ranking
import com.example.leaderboard.Rankings
import com.example.leaderboard.ShowLeaderboard
import com.example.player.PlayerId
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test

class ShowRankingsApiTests {

    private val authenticatedPlayerId = PlayerId()
    private val rankings = Rankings.InMemory()
    private val api = ShowLeaderboard()
        .asRoute(rankings)
        .authenticatedAs(authenticatedPlayerId)

    @Test
    fun `show the rankings for the current leaderboard`() {
        val alice = PlayerId()
        val bob = PlayerId()
        rankings.save(Ranking(alice, "alice", 50))
        rankings.save(Ranking(bob, "bob",100))

        val response = api(Request(GET, "/leaderboard"))

        with(response) {
            status shouldBe OK
            bodyString() shouldEqualSpecifiedJson """
            {
                "rankings": {
                    "bob": 100,
                    "alice": 50
                }
            }
            """.trimIndent()
        }
    }
}
