package com.example.player.infra

import com.example.player.EncodedPassword
import com.example.player.PlayerId
import com.example.player.RegisteredPlayer
import com.example.player.RegisteredPlayers
import io.kotest.matchers.Matcher
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

interface RegisteredPlayersContract {
    val players: RegisteredPlayers

    fun given(player: RegisteredPlayer): RegisteredPlayer
    fun haveBeenSaved(): Matcher<RegisteredPlayer>

    @Test
    fun `persist new registered players`() {
        val newPlayer = RegisteredPlayer(
            username = "new",
            password = EncodedPassword("encoded-new")
        )

        players.save(newPlayer)

        newPlayer should haveBeenSaved()
    }

    @Test
    fun `find persisted players by username`() {
        val existingPlayer = given(RegisteredPlayer(
            id = PlayerId(),
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        ))

        val foundPlayer = players.findByUsername(existingPlayer.username)!!

        foundPlayer shouldBeEqual existingPlayer
    }

    @Test
    fun `checks whether a player with a given username exists`() {
        given(RegisteredPlayer(
            id = PlayerId(),
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        ))

        players.existByUsername("existing-one") shouldBe true
        players.existByUsername("existing-two") shouldBe false
    }

    @Test
    fun `find persisted players by id`() {
        val existingPlayer = given(RegisteredPlayer(
            id = PlayerId(),
            username = "existing-one",
            password = EncodedPassword("encoded-existing-one")
        ))

        val foundPlayer = players.findById(existingPlayer.id)!!

        foundPlayer shouldBeEqual existingPlayer
    }

    @Test
    fun `returns nothing if cannot find a player`() {
        players.findByUsername("non-existing") shouldBe null
        players.findById(PlayerId()) shouldBe null
    }
}