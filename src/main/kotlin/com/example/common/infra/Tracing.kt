package com.example.common.infra

import org.http4k.core.then
import org.http4k.events.EventFilters.AddServiceName
import org.http4k.events.EventFilters.AddZipkinTraces
import org.http4k.events.Events
import org.http4k.events.HttpEvent.Incoming
import org.http4k.events.HttpEvent.Outgoing
import org.http4k.events.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.ResponseFilters.ReportHttpTransaction
import org.http4k.filter.ServerFilters

object OriginAwareEvents {
    operator fun invoke(origin: String, events: Events) = AddZipkinTraces()
        .then(AddServiceName(origin))
        .then(events)
}

object ClientTracing {
    operator fun invoke(events: Events) = ClientFilters.RequestTracing()
        .then(ReportHttpTransaction { events(Outgoing(it)) })
}

object ServerTracing {
    operator fun invoke(events: Events) = ServerFilters.RequestTracing()
        .then(ReportHttpTransaction { events(Incoming(it)) })
}
