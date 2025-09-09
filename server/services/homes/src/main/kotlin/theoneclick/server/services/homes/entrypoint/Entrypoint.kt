package theoneclick.server.services.homes.entrypoint

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import theoneclick.server.services.homes.di.AppComponent
import theoneclick.server.services.homes.plugins.configureRouting
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
    configureSessions()
    configureAuthentication(appComponent.environment, appComponent.encryptor, appComponent.logger)
    configureRouting(
        environment = appComponent.environment,
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
    configureShutdown(appComponent.onShutdown)
}
