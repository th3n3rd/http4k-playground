package com.example.gameplay.infra

import com.example.gameplay.Game
import com.example.gameplay.GameId
import com.example.gameplay.Games
import com.example.gameplay.SubmitGuess
import com.example.player.PlayerId
import com.example.player.infra.PlayerRequestContext.withPlayerId
import com.example.player.infra.authenticatedAs
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.kotest.shouldHaveStatus
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApprovalTest::class)
class SubmitGuessApiTests {

    private val authenticatedPlayerId = PlayerId.Placeholder
    private val anotherPlayerId = PlayerId()
    private val games = Games.InMemory()
    private val api = SubmitGuess(games)
        .asRoute(withPlayerId)
        .authenticatedAs(authenticatedPlayerId)

    @Test
    fun `mark the game as won when the guess is right`(approver: Approver) {
        val existingGame = Game(
            id = GameId.Placeholder,
            playerId = authenticatedPlayerId,
            secret = "correct"
        )
        games.save(existingGame)

        val response = api(Request(POST, "/games/${existingGame.id.value}/guesses").body("""
        {
            "secret": "correct"
        }            
        """.trimIndent()))

        response shouldHaveStatus CREATED
        approver.assertApproved(response)
        games.findByIdAndPlayerId(existingGame.id, authenticatedPlayerId)!!.won shouldBe true
    }

    @Test
    fun `fails when a game is not found for the authenticated player`() {
        val existingGame = Game(
            id = GameId.Placeholder,
            playerId = anotherPlayerId,
            secret = "correct",
        )
        games.save(existingGame)

        val response = api(Request(POST, "/games/${existingGame.id.value}/guesses").body("""
        {
            "secret": "correct"
        }            
        """.trimIndent()))

        response.status shouldBe NOT_FOUND
    }

    @Test
    fun `fails when the game is already completed`() {
        val completedGame = Game(
            id = GameId.Placeholder,
            playerId = authenticatedPlayerId,
            secret = "correct",
            guesses = listOf(
                Game.Guess("correct")
            )
        )
        games.save(completedGame)

        val response = api(Request(POST, "/games/${completedGame.id.value}/guesses").body("""
        {
            "secret": "correct"
        }            
        """.trimIndent()))

        response.status shouldBe BAD_REQUEST
    }
}
