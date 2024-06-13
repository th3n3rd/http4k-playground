package com.example.common.infra

import org.http4k.events.Event
import org.http4k.events.Events
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

fun EventsBus.Companion.InMemory(events: Events = {}) = InMemoryEventBus(events)

class InMemoryEventBus(private val events: Events): EventsBus {
    private val listeners = ConcurrentHashMap<KClass<out Event>, List<(Event) -> Unit>>()

    override operator fun <T : Event> invoke(eventType: KClass<T>, listener: (T) -> Unit) {
        listeners[eventType] = listeners[eventType].orEmpty() + {
            @Suppress("UNCHECKED_CAST")
            listener(it as T)
        }
    }

    override operator fun invoke(event: Event) {
        events(event)
        listeners[event::class]?.forEach { it(event) }
    }
}