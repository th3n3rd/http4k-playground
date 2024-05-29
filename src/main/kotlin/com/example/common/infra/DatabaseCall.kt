package com.example.common.infra

import org.http4k.events.Event

data class DatabaseCall(val database: String, val operation: String) : Event