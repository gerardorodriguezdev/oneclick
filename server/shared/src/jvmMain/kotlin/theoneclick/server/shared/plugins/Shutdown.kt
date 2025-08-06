package theoneclick.server.shared.plugins

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*

fun Application.configureShutdown(onShutdown: (application: Application) -> Unit) {
    install(Shutdown) {
        this.onShutdown = onShutdown
    }
}

@Suppress("ThrowExpression")
private val Shutdown = createApplicationPlugin("Shutdown", ::ShutdownConfiguration) {
    val onShutdown = pluginConfig.onShutdown

    on(MonitoringEvent(ApplicationStopped)) {
        onShutdown(it)
    }
}

private data class ShutdownConfiguration(
    var onShutdown: (application: Application) -> Unit = {},
)
