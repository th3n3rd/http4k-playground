package com.example.guessing.common.infra

import java.util.UUID

interface IdGenerator {
    operator fun invoke(): UUID

    companion object
}

class RandomIdGenerator : IdGenerator {
    override fun invoke(): UUID = UUID.randomUUID()
}

fun IdGenerator.Companion.Random() = RandomIdGenerator()