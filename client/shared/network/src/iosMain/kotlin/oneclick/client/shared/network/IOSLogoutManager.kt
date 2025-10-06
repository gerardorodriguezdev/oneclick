package oneclick.client.shared.network

import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.platform.LogoutManager

class IOSLogoutManager(
    private val navigationController: NavigationController,
    private val tokenDataSource: TokenDataSource,
) : LogoutManager {
    override suspend fun logout() {
        tokenDataSource.clear()
        navigationController.logout()
    }
}
