package com.example.common.infra

import org.flywaydb.core.Flyway

object RunDatabaseMigrations {
    operator fun invoke() {
        Flyway.configure()
            .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "example", "example")
            .load()
            .migrate()
    }
}
