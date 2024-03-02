package com.example

import io.kotest.assertions.json.schema.jsonSchema
import io.kotest.assertions.json.schema.obj
import io.kotest.assertions.json.schema.shouldMatchSchema
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beUUID
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotest::class)
class GetGameDetailsApiTests {

    private val games = InMemoryGames()
    private val api = GetGameDetailsApi(games)

    @Test
    fun `present the details of an existing game`() {
        val existingGame = Game(secret = "dont-care")
        games.save(existingGame)

        val response = api(Request(GET, "/games/${existingGame.id}"))

        with(response) {
            status shouldBe OK
            bodyString() shouldMatchSchema jsonSchema {
                obj {
                    obj {
                        string("value") { beUUID() }
                    }
                    string("hint")
                    boolean("won")
                }
            }
            bodyString() shouldEqualSpecifiedJson """
            {
                "hint": "_________",
                "won": false
            }
            """.trimIndent()
        }
    }
}
