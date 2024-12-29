package theoneclick.server.core.entrypoint

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import theoneclick.server.core.platform.base.Dependencies
import theoneclick.server.core.plugins.*
import theoneclick.server.core.plugins.authentication.configureAuthentication
import theoneclick.server.core.plugins.callid.configureCallId
import theoneclick.server.core.plugins.koin.configureKoin

fun server(dependencies: Dependencies): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> =
    embeddedServer(
        factory = CIO,
        port = 8080,
        module = {
            configureModules(dependencies)
        },
    )

fun Application.configureModules(dependencies: Dependencies) {
    configureKoin(dependencies)
    configureCallLogging()
    configureSerialization()
    configureAuthentication()
    configureSessions()
    configureRouting()
    configureStatusPages()
    configureRequestValidation()
    configureRequestBodyLimit()
    configureRateLimit()
    configureCallId()
}
