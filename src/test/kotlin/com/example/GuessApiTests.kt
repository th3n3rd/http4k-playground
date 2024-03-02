package com.example

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
class GuessApiTests {

    private val games = InMemoryGames()
    private val api = GuessApi(Guess(games))

    @Test
    fun `mark the game as won when the guess is right`() {
        val existingGame = Game(secret = "correct")
        games.save(existingGame)

        val response = api(Request(POST, "/games/${existingGame.id}/guesses").body("""
        {
            "secret": "correct"
        }            
        """.trimIndent()))

        with(response) {
            status shouldBe CREATED
            bodyString() shouldMatchSchema jsonSchema {
                obj {
                    obj {
                        string("id") { beUUID() }
                    }
                    string("hint")
                    boolean("won")
                }
            }
            bodyString() shouldEqualSpecifiedJson """
            {
                "hint": "_______",
                "won": true
            }
            """.trimIndent()
        }
        games.findById(existingGame.id)!!.won shouldBe true
    }

    @Test
    fun `fails when a game is not found`() {
        val response = api(Request(POST, "/games/${GameId()}/guesses").body("""
        {
            "secret": "correct"
        }            
        """.trimIndent()))

        response.status shouldBe NOT_FOUND
    }

    @Test
    fun `fails when the game is already completed`() {
        val completedGame = Game(secret = "correct", won = true)
        games.save(completedGame)

        val response = api(Request(POST, "/games/${completedGame.id}/guesses").body("""
        {
            "secret": "correct"
        }            
        """.trimIndent()))

        response.status shouldBe BAD_REQUEST
    }
}
