package theoneclick.server.app.entrypoint

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import theoneclick.server.app.di.AppComponent
import theoneclick.server.app.plugins.*
import theoneclick.server.app.plugins.authentication.configureAuthentication
import theoneclick.server.app.plugins.callid.configureCallId

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
        usersDataSource = appComponent.usersDataSource,
        encryptor = appComponent.encryptor,
        uuidProvider = appComponent.uuidProvider,
    )
    configureStatusPages(appComponent.logger)
    configureRequestValidation()
    configureRequestBodyLimit()
    configureRateLimit(appComponent.environment, appComponent.timeProvider)
    configureCallId(appComponent.timeProvider)
}
