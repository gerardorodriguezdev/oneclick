package theoneclick.client.core.testing.base

import kotlinx.coroutines.Dispatchers
import theoneclick.client.core.dataSources.AndroidInMemoryTokenDataSource
import theoneclick.client.core.dataSources.TokenDataSource
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.platform.AndroidAppDependencies
import theoneclick.client.core.platform.AppDependencies
import theoneclick.client.core.testing.fakes.fakeHttpClientEngine
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider

abstract class AndroidAppIntegrationTest : AppIntegrationTest() {
    private val dispatchersProvider = FakeDispatchersProvider(Dispatchers.Main)

    protected val tokenDataSource: TokenDataSource = AndroidInMemoryTokenDataSource()
    protected val navigationController: NavigationController = RealNavigationController()
    protected var isUserLogged: Boolean = false
    protected var isError: Boolean = false
    protected var devices: List<Device> = emptyList()

    private val httpClientEngine = fakeHttpClientEngine(
        isUserLogged = { isUserLogged },
        devices = { devices },
        isError = { isError },
    )
    override val appDependencies: AppDependencies = AndroidAppDependencies(
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        dispatchersProvider = dispatchersProvider,
        navigationController = navigationController,
    )
}