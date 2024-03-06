package com.example.common.infra

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target

object GenerateDatabaseMeta {
    operator fun invoke() {
        val config = Configuration()
            .withJdbc(
                Jdbc()
                    .withUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
                    .withUser("example")
                    .withPassword("example")
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
    RunDatabaseMigrations()
    GenerateDatabaseMeta()
}
