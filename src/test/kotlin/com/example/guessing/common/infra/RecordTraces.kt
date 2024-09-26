package com.example.guessing.common.infra

import org.http4k.tracing.*
import org.http4k.tracing.ActorType.*
import org.http4k.tracing.junit.ReportingMode.Companion.Always
import org.http4k.tracing.junit.TracerBulletEvents
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlInteractionDiagram
import org.http4k.tracing.renderer.PumlSequenceDiagram
import org.http4k.tracing.tracer.HttpTracer
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

private val resolveOrigin = ActorResolver {
    val service = it.metadata["service"].toString()
    val tags = it.metadata["tags"] as List<*>
    when {
        tags.contains("player") -> Actor(service, Human)
        else -> Actor(service, System)
    }
}

abstract class RecordTraces {
    @RegisterExtension
    val events = TracerBulletEvents(
        tracers = listOf(HttpTracer(resolveOrigin), RepositoryTracer(resolveOrigin)),
        renderers = listOf(PumlSequenceDiagram, PumlInteractionDiagram),
        traceRenderPersistence = TraceRenderPersistence.FileSystem(File("docs/system/generated")),
        reportingMode = Always
    )
}

object RepositoryTracer {
    operator fun invoke(resolveOrigin: ActorResolver) = Tracer { node, _ ->
        node.event
            .takeIf { it.event is RepositoryCall }
            ?.let {
                BiDirectional(
                    resolveOrigin(it),
                    Actor((it.event as RepositoryCall).database, Database),
                    (it.event as RepositoryCall).operation,
                    emptyList()
                )
            }
            ?.let { listOf(it) }
            ?: emptyList()
    }
}