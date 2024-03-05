package com.example.gameplay.infra

import com.example.ThreadSafetyTests
import com.example.gameplay.Secrets
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

@ThreadSafetyTests
class RotatingSecretsThreadSafetyTests {

    private val rotatingSecrets = Secrets.Rotating(listOf("first", "second"))

    @Operation
    fun nextSecret() = rotatingSecrets.next()

    @Test
    fun `is thread safe`() = StressOptions()
        .threads(2)
        .iterations(2)
        .check(this::class)
}
