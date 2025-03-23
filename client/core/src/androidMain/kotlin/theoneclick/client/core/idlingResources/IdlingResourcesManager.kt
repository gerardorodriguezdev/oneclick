package theoneclick.client.core.idlingResources

import io.ktor.client.plugins.api.*

val IdlingResourcesManager = createClientPlugin("idlingResourcesManager", ::IdlingResourcesManagerConfig) {
    val idlingResource = pluginConfig.idlingResource

    on(SetupRequest) { request ->
        idlingResource.increment()
    }

    onClose {
        idlingResource.decrement()
    }
}

class IdlingResourcesManagerConfig(
    var idlingResource: IdlingResource = EmptyIdlingResource(),
)