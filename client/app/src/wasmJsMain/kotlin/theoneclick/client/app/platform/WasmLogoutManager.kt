package theoneclick.client.app.platform

import theoneclick.client.app.navigation.NavigationController

class WasmLogoutManager(
    private val navigationController: NavigationController,
) : LogoutManager {
    override suspend fun logout() {
        navigationController.logout()
    }
}
