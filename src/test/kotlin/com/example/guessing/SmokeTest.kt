package com.example.guessing

import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.core.Uri
import org.http4k.events.Events
import org.http4k.lens.uri
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

@EnabledIfSystemProperty(named = "test", matches = "SmokeTest", disabledReason = "Smoke test is not enabled")
class SmokeTest : WinningGameplayJourney, TrackPerformancesJourney {
    override val events: Events = {}
    override val appUri: Uri = EnvironmentKey
        .uri()
        .defaulted("APP_URI", Uri.of("http://localhost:9000"))
        .invoke(ENV)
    override val randomisePlayerUsername = true
}