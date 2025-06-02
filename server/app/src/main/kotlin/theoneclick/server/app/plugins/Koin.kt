package theoneclick.server.app.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import theoneclick.server.app.platform.base.Dependencies
import theoneclick.server.app.platform.base.buildModule

fun Application.configureKoin(dependencies: Dependencies) {
    install(Koin) {
        modules(buildModule(dependencies))
    }
}