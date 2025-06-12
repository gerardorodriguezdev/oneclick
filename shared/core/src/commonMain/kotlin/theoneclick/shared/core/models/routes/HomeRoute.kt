package theoneclick.shared.core.models.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.routes.base.Route

sealed interface HomeRoute : Route {
    @Serializable
    sealed interface NavigationBarRoute : HomeRoute {

        @Serializable
        @SerialName("home--devices-list")
        data object DevicesList : NavigationBarRoute

        @Serializable
        @SerialName("home--user-settings")
        data object UserSettings : NavigationBarRoute
    }
}
