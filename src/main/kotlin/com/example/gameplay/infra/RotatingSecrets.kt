package com.example.gameplay.infra

import com.example.gameplay.Secrets
import java.util.concurrent.atomic.AtomicInteger

fun Secrets.Companion.Rotating(secrets: List<String>) = RotatingSecrets(secrets)

class RotatingSecrets(private val secrets: List<String>) : Secrets {
    private val position = AtomicInteger(0)

    override fun next(): String {
        return secrets[position.getAndIncrement() % secrets.size]
    }
}
