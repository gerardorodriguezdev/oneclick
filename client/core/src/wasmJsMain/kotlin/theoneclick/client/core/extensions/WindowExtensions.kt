package theoneclick.client.core.extensions

import org.w3c.dom.Location
import theoneclick.shared.core.routes.AppRoute

fun Location.toStartingRoute(): AppRoute =
    when (pathname) {
        AppRoute.Login.path -> AppRoute.Login
        AppRoute.Home.path -> AppRoute.Home
        else -> AppRoute.Login
    }
