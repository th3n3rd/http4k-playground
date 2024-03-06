package com.example.common.infra

import org.http4k.cloudnative.env.Environment
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.SQLDialect.H2
import org.jooq.impl.DSL.using

object DatabaseContext {
    operator fun invoke(environment: Environment, dialect: SQLDialect = H2): DSLContext {
        return using(DataSources(environment), dialect)
    }
}
