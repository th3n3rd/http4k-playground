package com.example

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind

fun PingApi() = "/ping" bind Method.GET to {
    Response(Status.OK).body("pong")
}
