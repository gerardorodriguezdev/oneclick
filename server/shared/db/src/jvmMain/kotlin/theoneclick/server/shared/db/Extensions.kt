package theoneclick.server.shared.db

import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

fun databaseDriver(
    jdbcUrl: String,
    postgresUsername: String,
    postgresPassword: String,
): JdbcDriver {
    val hikariConfig = HikariConfig().apply {
        this.jdbcUrl = jdbcUrl
        this.username = postgresUsername
        this.password = postgresPassword
        validate()
    }
    val hikariDataSource = HikariDataSource(hikariConfig)
    val driver = hikariDataSource.asJdbcDriver()
    return driver
}