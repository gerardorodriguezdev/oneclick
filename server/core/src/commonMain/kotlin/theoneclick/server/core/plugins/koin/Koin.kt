@file:Suppress("NoNonPrivateGlobalVariables")

package theoneclick.server.core.plugins.koin

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.util.*
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatformTools
import theoneclick.server.core.platform.base.Dependencies
import theoneclick.server.core.platform.base.buildModule

fun Application.configureKoin(dependencies: Dependencies) {
    install(Koin) {
        modules(buildModule(dependencies))
    }
}

/**
 * Temporal fix to support Ktor 3.0.0
 */
private val Koin = createApplicationPlugin(name = "Koin", createConfiguration = { KoinApplication.init() }) {
    val koinApplication = setupKoinApplication()
    KoinPlatformTools.defaultContext().getOrNull()?.let { stopKoin() } // for ktor auto-reload
    startKoin(koinApplication)
    setupMonitoring(koinApplication)
    setupKoinScope(koinApplication)
}

private fun PluginBuilder<KoinApplication>.setupKoinApplication(): KoinApplication {
    val koinApplication = pluginConfig
    koinApplication.createEagerInstances()
    application.setKoinApplication(koinApplication)
    return koinApplication
}

fun Application.setKoinApplication(koinApplication: KoinApplication) {
    attributes.put(KOIN_ATTRIBUTE_KEY, koinApplication)
}

private fun PluginBuilder<KoinApplication>.setupMonitoring(koinApplication: KoinApplication) {
    val monitor = application.monitor
    monitor.raise(KoinApplicationStarted, koinApplication)
    monitor.subscribe(ApplicationStopping) {
        monitor.raise(KoinApplicationStopPreparing, koinApplication)
        koinApplication.koin.close()
        monitor.raise(KoinApplicationStopped, koinApplication)
    }
}

private fun PluginBuilder<KoinApplication>.setupKoinScope(koinApplication: KoinApplication) {
    // Scope Handling
    on(CallSetup) { call ->
        val scopeComponent = RequestScope(koinApplication.koin)
        call.attributes.put(KOIN_SCOPE_ATTRIBUTE_KEY, scopeComponent.scope)
    }
    on(ResponseSent) { call ->
        call.attributes[KOIN_SCOPE_ATTRIBUTE_KEY].close()
    }
}

const val KOIN_KEY = "KOIN"
val KOIN_ATTRIBUTE_KEY = AttributeKey<KoinApplication>(KOIN_KEY)

const val KOIN_SCOPE_KEY = "KOIN_SCOPE"
val KOIN_SCOPE_ATTRIBUTE_KEY = AttributeKey<Scope>(KOIN_SCOPE_KEY)
