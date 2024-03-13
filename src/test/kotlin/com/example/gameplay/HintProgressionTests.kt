package com.example.gameplay

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class HintProgressionTests {

    @Test
    fun `reveals a given secret incrementally`() {
        val secret = "secret"
        HintProgression(secret, 0) shouldBe "______"
        HintProgression(secret, 1) shouldBe "s_____"
        HintProgression(secret, 2) shouldBe "s____t"
        HintProgression(secret, 3) shouldBe "se___t"
        HintProgression(secret, 4) shouldBe "se__et"
        HintProgression(secret, 5) shouldBe "sec_et"
        HintProgression(secret, 6) shouldBe "secret"
    }

    @Test
    fun `increasing the attempts over the secret length has no effect`() {
        HintProgression("secret", "secret".length + 10) shouldBe "secret"
    }
}
