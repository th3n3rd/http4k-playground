package com.example

import org.http4k.contract.ui.swaggerUiLite
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val app = routes(
        swaggerUiLite {
            url = "http://localhost:9000/docs/openapi.json"
            persistAuthorization = true
        }
    )

    app.asServer(SunHttp(9001))
        .start()
        .apply {
            println("Open API UI Server started on " + port())
        }
}