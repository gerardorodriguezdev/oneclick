package theoneclick.server.core.plugins.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.plugins.authentication.AuthenticationConstants.SESSION_AUTHENTICATION
import theoneclick.server.core.plugins.authentication.AuthenticationConstants.TOKEN_AUTHENTICATION
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.validators.ParamsValidator

fun Application.configureAuthentication() {
    val paramsValidator: ParamsValidator by inject()

    install(Authentication) {
        registerSessionAuthentication(paramsValidator)
        registerTokenAuthentication(paramsValidator)
    }
}

private fun AuthenticationConfig.registerSessionAuthentication(paramsValidator: ParamsValidator) {
    session<UserSession>(SESSION_AUTHENTICATION) {
        validate { userSession ->
            if (paramsValidator.isUserSessionValid(userSession)) userSession else null
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerTokenAuthentication(paramsValidator: ParamsValidator) {
    bearer(TOKEN_AUTHENTICATION) {
        realm = "Access to the '/' path"

        authenticate { tokenCredential ->
            val userSession = UserSession(tokenCredential.token)
            if (paramsValidator.isUserSessionValid(userSession)) userSession else null
        }
    }
}