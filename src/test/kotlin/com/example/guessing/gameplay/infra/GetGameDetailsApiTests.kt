package com.example.guessing.gameplay.infra

import com.example.guessing.gameplay.Game
import com.example.guessing.gameplay.GameId
import com.example.guessing.gameplay.Games
import com.example.guessing.gameplay.GetGameDetails
import com.example.guessing.player.PlayerId
import com.example.guessing.player.infra.PlayerRequestContext.withPlayerId
import com.example.guessing.player.infra.authenticatedAs
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApprovalTest::class)
class GetGameDetailsApiTests {

    private val anotherPlayer = PlayerId()
    private val authenticatedPlayerId = PlayerId()
    private val games = Games.InMemory()
    private val api = GetGameDetails(games)
        .asRoute(withPlayerId)
        .authenticatedAs(authenticatedPlayerId)

    @Test
    fun `present the details of an existing game`(approver: Approver) {
        val existingGame = Game(id = GameId.Placeholder, playerId = authenticatedPlayerId, secret = "dont-care")
        games.save(existingGame)

        val response = api(Request(GET, "/games/${existingGame.id.value}"))

        response shouldHaveStatus OK
        approver.assertApproved(response)
    }

    @Test
    fun `fails when the game does not exist for the authenticated player`() {
        val existingGame = Game(id = GameId.Placeholder, playerId = anotherPlayer)
        games.save(existingGame)

        val response = api(Request(GET, "/games/${existingGame.id.value}"))

        response.status shouldBe NOT_FOUND
    }
}
