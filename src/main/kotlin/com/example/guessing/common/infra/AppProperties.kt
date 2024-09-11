package com.example.guessing.common.infra

import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.nonEmptyString

object AppProperties {
    object DataSource {
        val Url = EnvironmentKey.nonEmptyString().defaulted("DATASOURCE_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
        val Username = EnvironmentKey.nonEmptyString().defaulted("DATASOURCE_USERNAME", "example")
        val Password = EnvironmentKey.nonEmptyString().defaulted("DATASOURCE_PASSWORD", "example")
    }
}
