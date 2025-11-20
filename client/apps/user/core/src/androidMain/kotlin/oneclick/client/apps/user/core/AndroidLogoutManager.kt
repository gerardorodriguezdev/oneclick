package oneclick.client.apps.user.core

import oneclick.client.apps.user.navigation.NavigationController
import oneclick.client.apps.user.navigation.logout
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.platform.LogoutManager

class AndroidLogoutManager(
    private val navigationController: NavigationController,
    private val tokenDataSource: TokenDataSource,
) : LogoutManager {
    override suspend fun logout() {
        tokenDataSource.clear()
        navigationController.logout()
    }
}