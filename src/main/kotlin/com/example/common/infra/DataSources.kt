package com.example.common.infra

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.http4k.cloudnative.env.Environment
import javax.sql.DataSource

object DataSources {
    operator fun invoke(environment: Environment): DataSource {
        return HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = AppProperties.DataSource.Url(environment)
                username = AppProperties.DataSource.Username(environment)
                password = AppProperties.DataSource.Password(environment)
            }
        )
    }
}
