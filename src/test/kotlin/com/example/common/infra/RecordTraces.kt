package com.example.common.infra

import org.http4k.tracing.Actor
import org.http4k.tracing.ActorResolver
import org.http4k.tracing.ActorType
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.junit.ReportingMode.Companion.Always
import org.http4k.tracing.junit.TracerBulletEvents
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlSequenceDiagram
import org.http4k.tracing.tracer.HttpTracer
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

private val actor = ActorResolver {
    Actor(it.metadata["service"].toString(), ActorType.System)
}

abstract class RecordTraces {
    @RegisterExtension
    val events = TracerBulletEvents(
        tracers = listOf(HttpTracer(actor)),
        renderers = listOf(PumlSequenceDiagram),
        traceRenderPersistence = TraceRenderPersistence.FileSystem(File("target/tracing")),
        reportingMode = Always
    )
}