package theoneclick.client.shared.network.platform

import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.network.dataSources.TokenDataSource

class AndroidLogoutManager(
    private val navigationController: NavigationController,
    private val tokenDataSource: TokenDataSource,
) : LogoutManager {
    override suspend fun logout() {
        tokenDataSource.clear()
        navigationController.logout()
    }
}
