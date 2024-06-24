package com.example

import com.example.common.infra.RecordTraces
import com.example.common.infra.RunDatabaseMigrations
import com.example.common.infra.TestEnvironment
import org.http4k.core.Uri

class JourneyTests : RecordTraces(), WinningGameplayJourney, TrackPerformancesJourney {

    private val env = TestEnvironment()

    init {
        RunDatabaseMigrations(env)
    }

    private val appServer = StartGuessTheSecretAppServer(
        environment = env,
        events = events
    )

    override val appUri: Uri = Uri.of("http://localhost:${appServer.port()}")
    override val randomisePlayerUsername: Boolean = false
}
