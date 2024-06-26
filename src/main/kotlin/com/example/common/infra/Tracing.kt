package com.example.common.infra

import org.http4k.core.then
import org.http4k.events.*
import org.http4k.events.EventFilters.AddServiceName
import org.http4k.events.EventFilters.AddTimestamp
import org.http4k.events.EventFilters.AddZipkinTraces
import org.http4k.events.HttpEvent.Incoming
import org.http4k.events.HttpEvent.Outgoing
import org.http4k.filter.ClientFilters
import org.http4k.filter.ResponseFilters.ReportHttpTransaction
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutedRequest

object AddTags {
    operator fun invoke(tags: List<String>) = EventFilter { next ->
        {
            next(it + ("tags" to tags))
        }
    }
}

object TracingEvents {
    operator fun invoke(origin: String, events: Events, tags: List<String> = emptyList()) = AddZipkinTraces()
        .then(AddTimestamp())
        .then(AddServiceName(origin))
        .then(AddTags(tags))
        .then(events.and(::println))
}

object ClientTracing {
    operator fun invoke(events: Events) = ClientFilters.RequestTracing()
        .then(ReportHttpTransaction { events(Outgoing(
            uri = it.request.uri,
            method = it.request.method,
            status = it.response.status,
            latency = it.duration.toMillis(),
            xUriTemplate = if (it.request is RoutedRequest) (it.request as RoutedRequest).xUriTemplate.toString() else it.request.uri.path.trimStart('/')
        )) })
}

object ServerTracing {
    operator fun invoke(events: Events) = ServerFilters.RequestTracing()
        .then(ReportHttpTransaction { events(Incoming(it)) })
}
