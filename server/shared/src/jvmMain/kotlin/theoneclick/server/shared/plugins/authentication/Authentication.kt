package theoneclick.server.shared.plugins.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import theoneclick.server.shared.dataSources.AuthenticationDataSource
import theoneclick.shared.contracts.core.models.Token

fun Application.configureAuthentication(authenticationDataSource: AuthenticationDataSource) {
    install(Authentication) {
        registerSessionAuthentication(authenticationDataSource)
        registerTokenAuthentication(authenticationDataSource)
    }
}

private fun AuthenticationConfig.registerSessionAuthentication(authenticationDataSource: AuthenticationDataSource) {
    session<Token>(AuthenticationConstants.SESSION_AUTHENTICATION) {
        validate { token ->
            if (authenticationDataSource.isUserSessionValid(token)) token else null
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerTokenAuthentication(authenticationDataSource: AuthenticationDataSource) {
    bearer(AuthenticationConstants.TOKEN_AUTHENTICATION) {
        realm = "Access to the '/' path"

        authenticate { tokenCredential ->
            if (authenticationDataSource.isUserSessionValid(tokenCredential.token)) {
                Token.unsafe(tokenCredential.token)
            } else {
                null
            }
        }
    }
}
