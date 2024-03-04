package com.example.gameplay

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class RotatingSecretsTests {

    private val rotatingSecrets = RotatingSecrets(listOf("first", "second"))

    @Operation
    fun nextSecret() = rotatingSecrets.next()

    @Test
    fun `is thread safe`() = StressOptions()
        .threads(2)
        .iterations(2)
        .check(this::class)
}
