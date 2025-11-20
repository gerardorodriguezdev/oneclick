package oneclick.client.apps.user.navigation.models.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import oneclick.client.apps.user.navigation.models.routes.base.Route

sealed interface HomeRoute : Route {
    @Serializable
    @SerialName("home--homes-list")
    data object HomesList : HomeRoute

    @Serializable
    @SerialName("home--user-settings")
    data object UserSettings : HomeRoute
}
