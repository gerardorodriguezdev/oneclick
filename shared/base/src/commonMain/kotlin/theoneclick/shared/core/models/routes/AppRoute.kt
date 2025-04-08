package theoneclick.shared.core.models.routes

import kotlinx.serialization.Serializable
import theoneclick.shared.core.models.routes.base.Route

@Serializable
sealed interface AppRoute : Route {

    @Serializable
    data object Init : AppRoute {
        override val path: String = "/"
    }

    @Serializable
    data object Login : AppRoute {
        override val path: String = "/login"
    }

    @Serializable
    data object Home : AppRoute {
        override val path: String = "/home"
    }
}
