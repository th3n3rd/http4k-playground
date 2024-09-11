package com.example.guessing.common.infra

import org.http4k.events.Event
import org.http4k.events.Events
import kotlin.reflect.KClass

interface EventsBus: Events {
    operator fun <T: Event> invoke(eventType: KClass<T>, listener: (T) -> Unit)

    companion object
}