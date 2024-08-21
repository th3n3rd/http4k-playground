package com.example.gameplay.infra

import com.example.common.infra.IdGenerator
import com.example.common.infra.Static
import com.example.player.infra.PlayerRequestContext.withPlayerId
import com.example.player.infra.authenticatedAs
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.StartNewGame
import com.example.player.PlayerId
import io.kotest.matchers.equals.shouldBeEqual
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.http4k.kotest.shouldHaveStatus
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApprovalTest::class)
class StartNewGameApiTests {

    private val authenticatedPlayerId = PlayerId()
    private val secrets = Secrets.Rotating(listOf("secret"))
    private val games = Games.InMemory()
    private val idGenerator = IdGenerator.Static()
    private val api = StartNewGame(games, secrets, idGenerator)
        .asRoute(withPlayerId)
        .authenticatedAs(authenticatedPlayerId)

    @Test
    fun `starts a new game for the authenticated player`(approver: Approver) {
        val response = api(Request(POST, "/games"))

        response shouldHaveStatus CREATED
        approver.assertApproved(response)
        val game = games.findAll().first()
        game.playerId shouldBeEqual authenticatedPlayerId
    }
}
