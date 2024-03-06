package com.example.common.infra

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target

object GenerateDatabaseMeta {
    operator fun invoke(environment: Environment) {
        val url = AppProperties.DataSource.Url(environment)
        val username = AppProperties.DataSource.Username(environment)
        val password = AppProperties.DataSource.Password(environment)

        val config = Configuration()
            .withJdbc(
                Jdbc()
                    .withUrl(url)
                    .withUser(username)
                    .withPassword(password)
            )
            .withGenerator(
                Generator()
                    .withName("org.jooq.codegen.KotlinGenerator")
                    .withDatabase(
                        Database()
                            .withName("org.jooq.meta.h2.H2Database")
                            .withIncludes(".*")
                            .withExcludes("")
                            .withInputSchema("PUBLIC")
                    )
                    .withTarget(
                        Target()
                            .withPackageName("com.example.common.infra.database")
                            .withDirectory("target/generated-sources/jooq")
                    )
            )
        GenerationTool.generate(config)
    }
}

fun main() {
    RunDatabaseMigrations(ENV)
    GenerateDatabaseMeta(ENV)
}
