package com.example

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.routing.bind

fun RegisterNewPlayerApi() = "/players" bind Method.POST to {
    Response(CREATED)
}
