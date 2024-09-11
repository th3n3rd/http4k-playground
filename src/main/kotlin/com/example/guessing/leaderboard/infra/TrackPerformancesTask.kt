package com.example.guessing.leaderboard.infra

import com.example.guessing.common.infra.EventsBus
import com.example.guessing.gameplay.GameCompleted
import com.example.guessing.leaderboard.TrackPerformances

class TrackPerformancesTask(trackPerformances: TrackPerformances, eventsBus: EventsBus) {
    init {
        eventsBus(GameCompleted::class) {
            trackPerformances(
                it.playerId,
                it.attempts
            )
        }
    }
}

fun TrackPerformances.asTask(eventsBus: EventsBus) = TrackPerformancesTask(this, eventsBus)