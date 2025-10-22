package oneclick.client.apps.user.core

import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.navigation.logout
import oneclick.client.shared.network.platform.LogoutManager

class WasmLogoutManager(
    private val navigationController: NavigationController,
) : LogoutManager {
    override suspend fun logout() {
        navigationController.logout()
    }
}