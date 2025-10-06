package oneclick.client.shared.network.platform

import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.navigation.NavigationController.NavigationEvent
import oneclick.client.shared.navigation.models.routes.AppRoute
import oneclick.client.shared.navigation.popUpToInclusive

interface LogoutManager {
    suspend fun logout()

    suspend fun NavigationController.logout() {
        sendNavigationEvent(
            NavigationEvent.Navigate(
                destinationRoute = AppRoute.Login,
                launchSingleTop = true,
                popUpTo = popUpToInclusive(startRoute = AppRoute.Home)
            )
        )
    }
}
