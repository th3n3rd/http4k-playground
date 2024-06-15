package com.example.gameplay.infra

import com.example.player.infra.PlayerRequestContext.withPlayerId
import com.example.player.infra.authenticatedAs
import com.example.gameplay.Game
import com.example.gameplay.Games
import com.example.gameplay.SubmitGuess
import com.example.player.PlayerId
import io.kotest.assertions.json.schema.jsonSchema
import io.kotest.assertions.json.schema.obj
import io.kotest.assertions.json.schema.shouldMatchSchema
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beUUID
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotest::class)
class SubmitGuessApiTests {

    private val authenticatedPlayerId = PlayerId()
    private val anotherPlayerId = PlayerId()
    private val games = Games.InMemory()
    private val api = SubmitGuess(games)
        .asRoute(withPlayerId)
        .authenticatedAs(authenticatedPlayerId)

    @Test
    fun `mark the game as won when the guess is right`() {
        val existingGame = Game(
            playerId = authenticatedPlayerId,
            secret = "correct"
        )
        games.save(existingGame)

        val response = api(Request(POST, "/games/${existingGame.id.value}/guesses").body("""
        {
            "secret": "correct"
        }            
        """.trimIndent()))

        with(response) {
            status shouldBe CREATED
            bodyString() shouldMatchSchema jsonSchema {
                obj {
                    string("id") { beUUID() }
                    string("playerId") { beUUID() }
                    string("hint")
                    boolean("won")
                    additionalProperties = false
                }
            }
            bodyString() shouldEqualSpecifiedJson """
            {
                "playerId": "${authenticatedPlayerId.value}",
                "hint": "c______",
                "won": true
            }
            """.trimIndent()
        }
        games.findByIdAndPlayerId(existingGame.id, authenticatedPlayerId)!!.won shouldBe true
    }

    @Test
    fun `fails when a game is not found for the authenticated player`() {
        val existingGame = Game(
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
