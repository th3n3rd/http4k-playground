package com.example.guessing.player

import com.example.guessing.player.EncodedPassword
import com.example.guessing.player.PlayerId
import com.example.guessing.player.RegisteredPlayer
import com.example.guessing.player.RegisteredPlayers
import io.kotest.matchers.Matcher
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

interface RegisteredPlayersContract {
    val players: RegisteredPlayers

    fun given(player: RegisteredPlayer): RegisteredPlayer
    fun haveBeenStored(): Matcher<RegisteredPlayer>

    @Test
    fun `store new registered players`() {
        val newPlayer = RegisteredPlayer(
            username = "new",
            password = EncodedPassword("encoded-new")
        )

        players.save(newPlayer)

        newPlayer should haveBeenStored()
    }

    @Test
    fun `find stored players by username`() {
        val existingPlayer = given(
            RegisteredPlayer(
            id = PlayerId(),
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        )
        )

        val foundPlayer = players.findByUsername(existingPlayer.username)!!

        foundPlayer shouldBeEqual existingPlayer
    }

    @Test
    fun `checks whether a player with a given username exists`() {
        given(
            RegisteredPlayer(
            id = PlayerId(),
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        )
        )

        players.existByUsername("existing-one") shouldBe true
        players.existByUsername("existing-two") shouldBe false
    }

    @Test
    fun `find stored players by id`() {
        val existingPlayer = given(
            RegisteredPlayer(
            id = PlayerId(),
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        )
        )

        val foundPlayer = players.findById(existingPlayer.id)!!

        foundPlayer shouldBeEqual existingPlayer
    }

    @Test
    fun `returns nothing if cannot find a player`() {
        players.findByUsername("non-existing") shouldBe null
        players.findById(PlayerId()) shouldBe null
    }
}