package theoneclick.server.shared.core.extensions

import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import theoneclick.server.shared.auth.models.JwtPayload
import theoneclick.server.shared.core.plugins.AuthenticationConstants
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.agents.Agent.Companion.toAgent

val RoutingRequest.agent: Agent
    get() {
        val userAgent = userAgent()
        return userAgent.toAgent()
    }

fun Routing.defaultAuthentication(
    optional: Boolean = false,
    block: Route.() -> Unit
): Route =
    authenticate(
        configurations = arrayOf(
            AuthenticationConstants.JWT_SESSION_AUTHENTICATION,
            AuthenticationConstants.JWT_AUTHENTICATION,
        ),
        optional = optional,
        build = block,
    )

fun RoutingContext.requireJwtPayload(): JwtPayload =
    requireNotNull(call.principal<JwtPayload>())

fun databaseDriver(
    jdbcUrl: String,
    postgresUsername: String,
    postgresPassword: String,
): JdbcDriver {
    val hikariConfig = HikariConfig().apply {
        this.jdbcUrl = jdbcUrl
        username = postgresUsername
        password = postgresPassword
        validate()
    }
    val hikariDataSource = HikariDataSource(hikariConfig)
    val driver = hikariDataSource.asJdbcDriver()
    return driver
}