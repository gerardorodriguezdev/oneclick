package theoneclick.client.app.platform

import theoneclick.client.app.dataSources.TokenDataSource
import theoneclick.client.app.navigation.NavigationController
import theoneclick.shared.core.platform.AppLogger

class AndroidLogoutManager(
    private val appLogger: AppLogger,
    private val navigationController: NavigationController,
    private val tokenDataSource: TokenDataSource,
) : LogoutManager {
    override suspend fun logout() {
        tokenDataSource.clear()
        navigationController.logout()
        appLogger.i("Logging out")
    }
}
