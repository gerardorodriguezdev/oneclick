package theoneclick.client.core.platform

import theoneclick.client.core.navigation.NavigationController

class WasmLogoutManager(
    private val navigationController: NavigationController,
) : LogoutManager {
    override suspend fun logout() {
        navigationController.logout()
    }
}
