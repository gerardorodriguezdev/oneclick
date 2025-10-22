package oneclick.client.app

import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.navigation.logout
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
