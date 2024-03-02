package com.example

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GameTests {

    @Test
    fun `contains an hint that is as long as the secret`() {
        val first = Game(secret = "a")
        val second = Game(secret = "abc")
        val third = Game(secret = "abcdefghijklmnopqrstuvwxyz")

        first.hint shouldBe "_"
        second.hint shouldBe "___"
        third.hint shouldBe "__________________________"
    }
}
