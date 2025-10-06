package oneclick.client.shared.network.platform

import oneclick.client.shared.navigation.NavigationController

class WasmLogoutManager(
    private val navigationController: NavigationController,
) : LogoutManager {
    override suspend fun logout() {
        navigationController.logout()
    }
}
