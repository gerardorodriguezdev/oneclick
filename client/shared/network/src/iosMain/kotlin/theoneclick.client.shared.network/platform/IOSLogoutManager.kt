package oneclick.client.shared.network.platform

import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.network.dataSources.TokenDataSource

class IOSLogoutManager(
    private val navigationController: NavigationController,
    private val tokenDataSource: TokenDataSource,
) : LogoutManager {
    override suspend fun logout() {
        tokenDataSource.clear()
        navigationController.logout()
    }
}
