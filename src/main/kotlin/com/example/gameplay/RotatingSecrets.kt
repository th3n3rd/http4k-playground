package com.example.gameplay

class RotatingSecrets(private val secrets: List<String> = listOf()) : Secrets {
    private var position = 0

    override fun next(): String {
        return secrets[position++ % secrets.size]
    }
}
