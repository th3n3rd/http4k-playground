package com.example.guessing.common.infra

import com.example.guessing.common.infra.EventsBus
import com.example.guessing.common.infra.InMemory
import io.kotest.matchers.shouldBe
import org.http4k.events.Event
import org.http4k.testing.RecordingEvents
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.DayOfWeek.*

class EventsBusTests {

    private val events = RecordingEvents()
    private val eventsBus = EventsBus.InMemory(events)

    data class ItHappened(val onDay: DayOfWeek = MONDAY): Event
    data class ItHappenedAgain(val onDay: DayOfWeek = MONDAY): Event

    @Test
    fun `invoke a consumer for a given event type`() {
        val invoked = mutableListOf<String>()
        eventsBus(ItHappened::class) { invoked.add("first") }

        eventsBus(ItHappened())

        invoked shouldBe listOf("first")
    }

    @Test
    fun `invoke all consumers for a given event type`() {
        val invoked = mutableListOf<String>()
        eventsBus(ItHappened::class) { invoked.add("first") }
        eventsBus(ItHappened::class) { invoked.add("second") }

        eventsBus(ItHappened())

        invoked shouldBe listOf("first", "second")
    }

    @Test
    fun `does not invoke consumers of other event types`() {
        val invoked = mutableListOf<String>()
        eventsBus(ItHappened::class) { invoked.add("first") }
        eventsBus(ItHappenedAgain::class) { invoked.add("second") }
        eventsBus(ItHappened::class) { invoked.add("third") }

        eventsBus(ItHappenedAgain())

        invoked shouldBe listOf("second")
    }

    @Test
    fun `delegates event propagation`() {
        eventsBus(ItHappened(MONDAY))
        eventsBus(ItHappenedAgain(TUESDAY))
        eventsBus(ItHappened(WEDNESDAY))

        events.toList() shouldBe listOf(
            ItHappened(MONDAY),
            ItHappenedAgain(TUESDAY),
            ItHappened(WEDNESDAY)
        )
    }
}