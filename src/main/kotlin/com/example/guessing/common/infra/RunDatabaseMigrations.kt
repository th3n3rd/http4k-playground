package com.example.guessing.common.infra

import org.flywaydb.core.Flyway
import org.http4k.cloudnative.env.Environment

object RunDatabaseMigrations {
    operator fun invoke(environment: Environment) {
        val url = AppProperties.DataSource.Url(environment)
        val username = AppProperties.DataSource.Username(environment)
        val password = AppProperties.DataSource.Password(environment)

        Flyway.configure()
            .dataSource(url, username, password)
            .load()
            .migrate()
    }
}
