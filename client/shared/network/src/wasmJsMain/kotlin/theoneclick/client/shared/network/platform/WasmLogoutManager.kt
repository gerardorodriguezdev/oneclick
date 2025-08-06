package theoneclick.client.shared.network.platform

import theoneclick.client.shared.navigation.NavigationController

class WasmLogoutManager(
    private val navigationController: NavigationController,
) : LogoutManager {
    override suspend fun logout() {
        navigationController.logout()
    }
}
