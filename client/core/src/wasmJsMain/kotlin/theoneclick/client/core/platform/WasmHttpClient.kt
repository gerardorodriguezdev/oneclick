package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.plugins.LogoutManager
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.core.models.routes.AppRoute

fun wasmHttpClient(navigationController: NavigationController): HttpClient {
    val engine = Js.create()

    return HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)

            userAgent(Agent.BROWSER.value)
        }

        install(LogoutManager) {
            onLogout = {
                navigationController.sendNavigationEvent(
                    NavigationController.NavigationEvent.Navigate(
                        destinationRoute = AppRoute.Login,
                        launchSingleTop = true,
                        popUpTo = popUpToInclusive(startRoute = AppRoute.Init)
                    )
                )
            }
        }
    }
}