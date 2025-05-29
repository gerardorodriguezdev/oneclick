package theoneclick.server.app.plugins.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import theoneclick.server.app.models.UserSession
import theoneclick.server.app.plugins.authentication.AuthenticationConstants.SESSION_AUTHENTICATION
import theoneclick.server.app.plugins.authentication.AuthenticationConstants.TOKEN_AUTHENTICATION
import org.koin.ktor.ext.inject
import theoneclick.server.app.validators.ParamsValidator

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
            if (paramsValidator.isUserSessionValid(userSession.sessionToken)) userSession else null
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
            if (paramsValidator.isUserSessionValid(tokenCredential.token)) UserSession(tokenCredential.token) else null
        }
    }
}
