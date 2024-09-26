package com.example.guessing.common.infra

import org.http4k.events.Event

data class RepositoryCall(val database: String, val operation: String) : Event