package theoneclick.shared.core.models.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.routes.base.Route

@Serializable
sealed interface HomeRoute : Route {

    @Serializable
    @SerialName("home--devices-list")
    data object DevicesList : HomeRoute

    @Serializable
    @SerialName("home--add-device")
    data object AddDevice : HomeRoute

    @Serializable
    @SerialName("home--user-settings")
    data object UserSettings : HomeRoute
}
