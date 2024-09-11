package com.example.guessing.gameplay

import com.example.guessing.gameplay.HintProgression
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class HintProgressionTests {

    @Test
    fun `reveals a given secret incrementally`() {
        val secret = "secret"
        HintProgression(secret, attempts = 0) shouldBe "______"
        HintProgression(secret, attempts = 1) shouldBe "s_____"
        HintProgression(secret, attempts = 2) shouldBe "s____t"
        HintProgression(secret, attempts = 3) shouldBe "se___t"
        HintProgression(secret, attempts = 4) shouldBe "se__et"
        HintProgression(secret, attempts = 5) shouldBe "sec_et"
        HintProgression(secret, attempts = 6) shouldBe "secret"
    }

    @Test
    fun `increasing the attempts over the secret length has no effect`() {
        HintProgression("secret", "secret".length + 10) shouldBe "secret"
    }
}
