package com.example

import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.routing.bind

fun GuessApi() = "/games/{id}/guesses" bind POST to {
    Response(CREATED)
}
