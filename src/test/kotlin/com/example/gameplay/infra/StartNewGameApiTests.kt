package com.example.gameplay.infra

import com.example.common.infra.PlayerAuthenticated
import com.example.gameplay.Games
import com.example.gameplay.Secrets
import com.example.gameplay.StartNewGame
import com.example.player.PlayerId
import io.kotest.assertions.json.schema.jsonSchema
import io.kotest.assertions.json.schema.obj
import io.kotest.assertions.json.schema.shouldMatchSchema
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beUUID
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.then
import org.junit.jupiter.api.Test

@OptIn(ExperimentalKotest::class)
class StartNewGameApiTests {

    private val authenticatedPlayerId = PlayerId()
    private val secrets = Secrets.Rotating(listOf("secret"))
    private val games = Games.InMemory()
    private val api = PlayerAuthenticated(authenticatedPlayerId)
        .then(StartNewGame(games, secrets).asRoute(PlayerAuthenticated.playerIdLens))

    @Test
    fun `starts a new game for the authenticated player`() {
        val response = api(Request(POST, "/games"))

        with(response) {
            status shouldBe CREATED
            bodyString() shouldMatchSchema jsonSchema {
                obj {
                    string("id") { beUUID() }
                    additionalProperties = false
                }
            }
        }
        val game = games.findAll().first()
        game.playerId shouldBeEqual authenticatedPlayerId
    }
}
