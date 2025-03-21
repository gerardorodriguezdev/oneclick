package theoneclick.server.core.plugins.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.validators.ParamsValidator
import theoneclick.shared.core.models.routes.AppRoute

fun Application.configureAuthentication() {
    val paramsValidator: ParamsValidator by inject()

    install(Authentication) {
        registerSessionAuthentication(paramsValidator)
    }
}

private fun AuthenticationConfig.registerSessionAuthentication(paramsValidator: ParamsValidator) {
    session<UserSession>(AuthenticationConstants.SESSION_AUTHENTICATION) {
        validate { userSession ->
            if (paramsValidator.isUserSessionValid(userSession)) userSession else null
        }

        challenge {
            call.sessions.clear<UserSession>()
            call.respondRedirect(AppRoute.Login.path) //TODO: How to handle?
        }
    }
}