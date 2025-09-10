package theoneclick.server.shared.core

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import theoneclick.server.shared.core.di.Dependencies
import theoneclick.server.shared.core.plugins.*

fun server(
    dependencies: Dependencies,
    configureModules: Application.() -> Unit,
    onShutdown: (application: Application) -> Unit = {},
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
    embeddedServer(
        factory = Netty,
        port = 8080,
        module = {
            configureBaseModules(dependencies = dependencies, onShutdown = onShutdown)
            configureModules()
        },
    )

private fun Application.configureBaseModules(
    dependencies: Dependencies,
    onShutdown: (application: Application) -> Unit
) {
    configureCallLogging(logger = dependencies.logger, timeProvider = dependencies.timeProvider)
    configureSerialization()
    configureSessions(dependencies.jwtProvider)
    configureAuthentication(
        jwtProvider = dependencies.jwtProvider,
        logger = dependencies.logger
    )
    configureStatusPages(dependencies.logger)
    configureRequestValidation()
    configureRequestBodyLimit()
    configureRateLimit(disableRateLimit = dependencies.disableRateLimit, timeProvider = dependencies.timeProvider)
    configureCallId(dependencies.timeProvider)
    configureCompression(dependencies.baseUrl)
    configureShutdown(onShutdown)
    configureHealthz(dependencies.healthzPath)
}
