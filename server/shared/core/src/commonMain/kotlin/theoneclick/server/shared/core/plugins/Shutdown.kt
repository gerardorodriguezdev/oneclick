package theoneclick.server.shared.core.plugins

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*

internal fun Application.configureShutdown(onShutdown: (application: Application) -> Unit) {
    install(Shutdown) {
        this.onShutdown = onShutdown
    }
}

private val Shutdown = createApplicationPlugin("Shutdown", ::ShutdownConfiguration) {
    val onShutdown = pluginConfig.onShutdown

    on(MonitoringEvent(ApplicationStopped)) {
        onShutdown(it)
    }
}

private data class ShutdownConfiguration(
    var onShutdown: (application: Application) -> Unit = {},
)
