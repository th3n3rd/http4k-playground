package com.example.guessing.common.infra

import com.example.guessing.common.infra.IdGenerator
import java.util.*

private val placeholder = UUID.fromString("00000000-0000-0000-0000-000000000000")

class StaticIdGenerator(private val value: UUID) : IdGenerator {
    override fun invoke(): UUID = value
}

fun IdGenerator.Companion.Static(value: UUID = placeholder) = StaticIdGenerator(value)