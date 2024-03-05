package com.example.common.infra

object DatabaseMigrations {
    operator fun invoke() {
        org.flywaydb.core.Flyway.configure()
            .dataSource("jdbc:h2:mem:testdb", "example", "example")
            .load()
            .migrate()
    }
}
