package com.example.guessing

import com.example.guessing.common.infra.*
import com.example.guessing.gameplay.Games
import com.example.guessing.gameplay.Secrets
import com.example.guessing.gameplay.infra.Database
import com.example.guessing.gameplay.infra.InMemory
import com.example.guessing.gameplay.infra.Rotating
import com.example.guessing.leaderboard.Rankings
import com.example.guessing.leaderboard.infra.InMemory
import com.example.guessing.player.PasswordEncoder
import com.example.guessing.player.RegisteredPlayers
import com.example.guessing.player.infra.Argon2
import com.example.guessing.player.infra.Database
import com.example.guessing.player.infra.InMemory
import org.http4k.cloudnative.env.Environment
import org.http4k.events.Events

data class AppContext(
    val eventsBus: EventsBus = EventsBus.InMemory(),
    val players: RegisteredPlayers = RegisteredPlayers.InMemory(),
    val games: Games = Games.InMemory(),
    val secrets: Secrets = Secrets.Rotating(listOf("correct")),
    val rankings: Rankings = Rankings.InMemory(),
    val passwordEncoder: PasswordEncoder = PasswordEncoder.Argon2()
) {
    companion object
}

fun AppContext.Companion.Prod(environment: Environment, events: Events = {}): AppContext {
    val database = DatabaseContext(environment)
    val appEvents = TracingEvents("app", events)

    return AppContext(
        eventsBus = EventsBus.InMemory(appEvents),
        players = RepositoryTracing<RegisteredPlayers>(RegisteredPlayers.Database(database), appEvents),
        games = RepositoryTracing<Games>(Games.Database(database), appEvents),
        secrets = Secrets.Rotating(listOf("correct")),
        rankings = RepositoryTracing<Rankings>(Rankings.InMemory(), appEvents),
        passwordEncoder = PasswordEncoder.Argon2(),
    )
}