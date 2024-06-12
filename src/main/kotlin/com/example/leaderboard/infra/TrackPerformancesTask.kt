package com.example.leaderboard.infra

import com.example.common.infra.EventsBus
import com.example.gameplay.GameCompleted
import com.example.leaderboard.TrackPerformances

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