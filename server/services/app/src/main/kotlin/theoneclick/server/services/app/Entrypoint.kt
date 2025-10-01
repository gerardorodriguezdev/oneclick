package theoneclick.server.services.app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import theoneclick.server.services.app.di.Dependencies
import theoneclick.server.services.app.plugins.*
import theoneclick.server.services.app.plugins.authentication.configureAuthentication

internal fun server(dependencies: Dependencies): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
    embeddedServer(
        factory = Netty,
        port = 8080,
        module = {
            configureModules(dependencies = dependencies)
        },
    )

private fun Application.configureModules(dependencies: Dependencies) {
    configureCallLogging(logger = dependencies.logger, timeProvider = dependencies.timeProvider)
    configureSerialization()
    configureSessions(dependencies.jwtProvider)
    configureAuthentication(
        jwtProvider = dependencies.jwtProvider,
        logger = dependencies.logger,
        invalidJwtDataSource = dependencies.invalidJwtDataSource,
    )
    configureStatusPages(dependencies.logger)
    configureRequestValidation()
    configureRequestBodyLimit()
    configureRateLimit(disableRateLimit = dependencies.disableRateLimit, timeProvider = dependencies.timeProvider)
    configureCallId(dependencies.uuidProvider)
    configureCompression(dependencies.baseUrl)
    configureShutdown(dependencies.onShutdown)
    configureRouting(
        usersRepository = dependencies.usersRepository,
        encryptor = dependencies.encryptor,
        jwtProvider = dependencies.jwtProvider,
        uuidProvider = dependencies.uuidProvider,
        homesRepository = dependencies.homesRepository,
        invalidJwtDataSource = dependencies.invalidJwtDataSource,
    )
}