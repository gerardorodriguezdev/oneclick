package theoneclick.client.shared.navigation.models.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import theoneclick.client.shared.navigation.models.routes.base.Route

sealed interface HomeRoute : Route {
    @Serializable
    sealed interface NavigationBarRoute : HomeRoute {

        @Serializable
        @SerialName("home--homes-list")
        data object HomesList : NavigationBarRoute

        @Serializable
        @SerialName("home--user-settings")
        data object UserSettings : NavigationBarRoute
    }
}
