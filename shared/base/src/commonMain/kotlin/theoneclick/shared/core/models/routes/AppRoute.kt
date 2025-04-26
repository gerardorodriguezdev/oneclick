package theoneclick.shared.core.models.routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.routes.base.Route

@Serializable
sealed interface AppRoute : Route {

    @Serializable
    @SerialName("init")
    data object Init : AppRoute

    @Serializable
    @SerialName("login")
    data object Login : AppRoute

    @Serializable
    @SerialName("home")
    data object Home : AppRoute
}
