package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.client.core.dataSources.TokenDataSource
import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.client.core.idlingResources.IdlingResource
import theoneclick.client.core.idlingResources.IdlingResourcesManager
import theoneclick.client.core.mappers.urlProtocol
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.plugins.LogoutManager
import theoneclick.client.core.plugins.TokenManager
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.core.models.routes.AppRoute

fun androidHttpClient(
    httpClientEngine: HttpClientEngine,
    tokenDataSource: TokenDataSource,
    idlingResource: IdlingResource,
    navigationController: NavigationController,
): HttpClient {
    return HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json()
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)

            val urlProtocol = BuildKonfig.urlProtocol()
            urlProtocol?.let {
                url.protocol = urlProtocol
            }

            BuildKonfig.HOST?.let { host ->
                this.host = host
            }

            BuildKonfig.PORT?.let { port ->
                this.port = port
            }

            userAgent(Agent.MOBILE.value)
        }

        install(TokenManager) {
            this.tokenDataSource = tokenDataSource
        }

        install(IdlingResourcesManager) {
            this.idlingResource = idlingResource
        }

        install(LogoutManager) {
            onLogout = {
                tokenDataSource.clear()

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
