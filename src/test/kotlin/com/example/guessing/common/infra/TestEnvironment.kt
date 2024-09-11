package com.example.guessing.common.infra

import java.util.*
import org.http4k.cloudnative.env.Environment

object TestEnvironment {
    operator fun invoke(): Environment {
        return Environment.from(
            "DATASOURCE_URL" to "jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1",
            "DATASOURCE_USERNAME" to "example",
            "DATASOURCE_PASSWORD" to "example",
        )
    }
}
