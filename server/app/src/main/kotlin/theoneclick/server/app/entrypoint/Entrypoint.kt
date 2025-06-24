package theoneclick.server.app.entrypoint

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import theoneclick.server.app.di.AppComponent
import theoneclick.server.app.plugins.*
import theoneclick.server.shared.plugins.authentication.configureAuthentication
import theoneclick.server.shared.plugins.configureCallId
import theoneclick.server.shared.plugins.configureCallLogging
import theoneclick.server.shared.plugins.configureCompression
import theoneclick.server.shared.plugins.configureRateLimit
import theoneclick.server.shared.plugins.configureRequestBodyLimit
import theoneclick.server.shared.plugins.configureRequestValidation
import theoneclick.server.shared.plugins.configureSerialization
import theoneclick.server.shared.plugins.configureSessions
import theoneclick.server.shared.plugins.configureStatusPages

fun server(appComponent: AppComponent): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
    embeddedServer(
        factory = Netty,
        port = 8080,
        module = {
            configureModules(appComponent)
        },
    )

private fun Application.configureModules(appComponent: AppComponent) {
    configureCallLogging(appComponent.logger, appComponent.timeProvider)
    configureSerialization()
    configureAuthentication(appComponent.authenticationDataSource)
    configureSessions(appComponent.environment, appComponent.ivGenerator)
    configureRouting(
        environment = appComponent.environment,
        usersRepository = appComponent.usersRepository,
        encryptor = appComponent.encryptor,
        uuidProvider = appComponent.uuidProvider,
        homesRepository = appComponent.homesRepository,
    )
    configureStatusPages(appComponent.logger)
    configureRequestValidation()
    configureRequestBodyLimit()
    configureRateLimit(appComponent.environment, appComponent.timeProvider)
    configureCallId(appComponent.timeProvider)
    configureCompression(appComponent.environment)
}
