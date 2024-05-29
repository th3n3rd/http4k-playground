package com.example.common.infra

import org.http4k.tracing.Actor
import org.http4k.tracing.ActorResolver
import org.http4k.tracing.ActorType.Database
import org.http4k.tracing.ActorType.System
import org.http4k.tracing.BiDirectional
import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.Tracer
import org.http4k.tracing.junit.ReportingMode.Companion.Always
import org.http4k.tracing.junit.TracerBulletEvents
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlSequenceDiagram
import org.http4k.tracing.tracer.HttpTracer
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

private val resolveOrigin = ActorResolver {
    Actor(it.metadata["service"].toString(), System)
}

abstract class RecordTraces {
    @RegisterExtension
    val events = TracerBulletEvents(
        tracers = listOf(HttpTracer(resolveOrigin), DatabaseTracer(resolveOrigin)),
        renderers = listOf(PumlSequenceDiagram),
        traceRenderPersistence = TraceRenderPersistence.FileSystem(File("target/tracing")),
        reportingMode = Always
    )
}

object DatabaseTracer {
    operator fun invoke(resolveOrigin: ActorResolver) = Tracer { node, _ ->
        node.event
            .takeIf { it.event is DatabaseCall }
            ?.let {
                BiDirectional(
                    resolveOrigin(it),
                    Actor((it.event as DatabaseCall).database, Database),
                    (it.event as DatabaseCall).operation,
                    emptyList()
                )
            }
            ?.let { listOf(it) }
            ?: emptyList()
    }
}