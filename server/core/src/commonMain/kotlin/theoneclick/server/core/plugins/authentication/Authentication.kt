package theoneclick.server.core.plugins.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.plugins.authentication.AuthenticationConstants.AUTH_SESSION
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.validators.ParamsValidator

fun Application.configureAuthentication() {
    val paramsValidator: ParamsValidator by inject()

    install(Authentication) {
        registerSessionAuthentication(paramsValidator)
    }
}

private fun AuthenticationConfig.registerSessionAuthentication(paramsValidator: ParamsValidator) {
    session<UserSession>(AUTH_SESSION) {
        validate { userSession ->
            if (paramsValidator.isUserSessionValid(userSession)) userSession else null
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}