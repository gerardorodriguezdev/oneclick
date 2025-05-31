package theoneclick.server.app.entrypoint

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import theoneclick.server.app.platform.base.Dependencies
import theoneclick.server.app.plugins.*
import theoneclick.server.app.plugins.authentication.configureAuthentication
import theoneclick.server.app.plugins.callid.configureCallId
import theoneclick.server.core.theoneclick.server.app.plugins.configureKoin

fun server(dependencies: Dependencies): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> =
    embeddedServer(
        factory = CIO,
        port = 8080,
        module = {
            configureModules(dependencies)
        },
    )

private fun Application.configureModules(dependencies: Dependencies) {
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
    configureCSFR()
}
