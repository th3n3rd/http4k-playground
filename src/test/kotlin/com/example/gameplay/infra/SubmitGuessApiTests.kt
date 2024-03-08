package com.example.gameplay.infra

import com.example.common.infra.PlayerAuthenticated
import com.example.gameplay.Game
import com.example.gameplay.GameId
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
import org.http4k.core.then
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotest::class)
class SubmitGuessApiTests {

    private val authenticatedPlayerId = PlayerId()
    private val games = Games.InMemory()
    private val api = PlayerAuthenticated(authenticatedPlayerId)
        .then(SubmitGuessApi(SubmitGuess(games), PlayerAuthenticated.playerIdLens))

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
                "hint": "_______",
                "won": true
            }
            """.trimIndent()
        }
        games.findById(existingGame.id)!!.won shouldBe true
    }

    @Test
    fun `fails when a game is not found`() {
        val response = api(Request(POST, "/games/${GameId().value}/guesses").body("""
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
            won = true
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
