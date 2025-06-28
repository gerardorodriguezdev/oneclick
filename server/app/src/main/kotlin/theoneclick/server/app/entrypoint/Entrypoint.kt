package theoneclick.server.app.entrypoint

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import theoneclick.server.app.di.AppComponent
import theoneclick.server.app.plugins.configureRouting
import theoneclick.server.shared.plugins.*
import theoneclick.server.shared.plugins.authentication.configureAuthentication

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
        sessionsRepository = appComponent.sessionsRepository,
    )
    configureStatusPages(appComponent.logger)
    configureRequestValidation()
    configureRequestBodyLimit()
    configureRateLimit(appComponent.environment, appComponent.timeProvider)
    configureCallId(appComponent.timeProvider)
    configureCompression(appComponent.environment)
    configureShutdown(appComponent.onShutdown)
}
