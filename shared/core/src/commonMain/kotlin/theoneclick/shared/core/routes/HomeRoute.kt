package theoneclick.shared.core.routes

import kotlinx.serialization.Serializable
import theoneclick.shared.core.routes.base.Route

@Serializable
sealed interface HomeRoute : Route {

    @Serializable
    data object DevicesList : HomeRoute {
        override val path: String = "/home/devices-list"
    }

    @Serializable
    data object AddDevice : HomeRoute {
        override val path: String = "/home/add-device"
    }
}
