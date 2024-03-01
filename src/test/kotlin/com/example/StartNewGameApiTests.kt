package com.example

import io.kotest.assertions.json.schema.jsonSchema
import io.kotest.assertions.json.schema.obj
import io.kotest.assertions.json.schema.shouldMatchSchema
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beUUID
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotest::class)
class StartNewGameApiTests {

    private val games = InMemoryGames()
    private val api = StartNewGameApi(StartNewGame(games))

    @Test
    fun `starts a new game`() {
        val response = api(Request(POST, "/games"))

        with(response) {
            status shouldBe CREATED
            bodyString() shouldMatchSchema jsonSchema {
                obj {
                    string("value") { beUUID() }
                }
            }
        }
        games.findAll() shouldNot beEmpty()
    }
}