package com.example.gameplay

import java.util.concurrent.atomic.AtomicInteger

class RotatingSecrets(private val secrets: List<String> = listOf()) : Secrets {
    private val position = AtomicInteger(0)

    override fun next(): String {
        return secrets[position.getAndIncrement() % secrets.size]
    }
}
