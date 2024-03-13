package com.example.gameplay

import com.example.gameplay.infra.InMemory
import com.example.gameplay.infra.Rotating
import com.example.player.PlayerId
import com.example.player.Players.anyPlayer
import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class StartNewGameTests {

    private val secrets = Secrets.Rotating(listOf("first", "second"))
    private val games = Games.InMemory()
    private val startNewGame = StartNewGame(games, secrets)

    @Test
    fun `starts a different game every time`() {
        val firstPlayer = PlayerId()
        val secondPlayer = PlayerId()

        val firstGame = startNewGame(firstPlayer)
        val secondGame = startNewGame(firstPlayer)
        val thirdGame = startNewGame(secondPlayer)

        firstGame shouldNotBeEqual secondGame
        secondGame shouldNotBeEqual thirdGame
        firstGame shouldNotBeEqual thirdGame
    }

    @Test
    fun `new games get a random secret to guess`() {
        val first = startNewGame(PlayerId())
        val second = startNewGame(PlayerId())

        first.secret shouldNotBeEqual second.secret
    }

    @Test
    fun `new games have no attempts recorded yet`() {
        val newGame = startNewGame(PlayerId())

        newGame.attempts shouldBe 0
    }

    @Test
    fun `new games get persisted`() {
        val first = startNewGame(PlayerId())
        val second = startNewGame(PlayerId())

        games.findAll() should containOnly(first, second)
    }

    @Test
    fun `new games are associated with the player who start them`() {
        val firstPlayer = PlayerId()
        val secondPlayer = PlayerId()

        val firstGame = startNewGame(firstPlayer)
        val secondGame = startNewGame(secondPlayer)

        firstGame.playerId shouldBeEqual firstPlayer
        secondGame.playerId shouldBeEqual secondPlayer
    }
}
