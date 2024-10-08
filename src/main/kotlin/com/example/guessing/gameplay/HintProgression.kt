package com.example.guessing.gameplay

import kotlin.math.min

object HintProgression {
    private const val Placeholder = "_"

    operator fun invoke(secret: String, attempts: Int): String {
        val cappedAttempts = min(attempts, secret.length)
        val hint = initialHint(secret)
        revealFromLeft(cappedAttempts, hint, secret)
        revealFromRight(cappedAttempts, hint, secret)
        return hint.toString()
    }

    private fun initialHint(secret: String) = StringBuilder(Placeholder.repeat(secret.length))

    private fun revealFromLeft(attempts: Int, hint: StringBuilder, secret: String) {
        val left = (attempts + 1) / 2
        for (i in 0 until left) {
            hint.setCharAt(i, secret[i])
        }
    }

    private fun revealFromRight(attempts: Int, hint: StringBuilder, secret: String) {
        val right = attempts / 2
        for (i in 0 until right) {
            hint.setCharAt(secret.lastIndex - i, secret[secret.lastIndex - i])
        }
    }
}
