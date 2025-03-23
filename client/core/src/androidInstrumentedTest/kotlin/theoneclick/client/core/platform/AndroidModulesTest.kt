package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verifyAll
import theoneclick.client.core.dataSources.AndroidLocalTokenDataSource
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.navigation.NavigationController
import theoneclick.shared.core.models.routes.AppRoute
import theoneclick.shared.timeProvider.SystemTimeProvider

class AndroidModulesTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun GIVEN_defaultModules_THEN_instancesAreCreatedCorrectly() {
        val appEntrypoint = AppEntrypoint()
        val modules = appEntrypoint.buildAppModules(
            appDependencies = AndroidAppDependencies(
                httpClientEngine = androidHttpClientEngine(
                    timeProvider = SystemTimeProvider(),
                ),
                tokenDataSource = AndroidLocalTokenDataSource(),
            )
        )

        modules.verifyAll(
            listOf(
                HttpClientEngine::class,
                HttpClientConfig::class,
                NavigationController::class,
                AuthenticationDataSource::class,
                AppRoute::class,
            )
        )
    }
}
