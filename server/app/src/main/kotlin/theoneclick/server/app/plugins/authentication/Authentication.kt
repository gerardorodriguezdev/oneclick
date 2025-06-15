package theoneclick.server.app.plugins.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import theoneclick.server.app.dataSources.AuthenticationDataSource
import theoneclick.server.app.models.Token
import theoneclick.server.app.plugins.authentication.AuthenticationConstants.SESSION_AUTHENTICATION
import theoneclick.server.app.plugins.authentication.AuthenticationConstants.TOKEN_AUTHENTICATION

fun Application.configureAuthentication() {
    val authenticationDataSource by inject<AuthenticationDataSource>()
    install(Authentication) {
        registerSessionAuthentication(authenticationDataSource)
        registerTokenAuthentication(authenticationDataSource)
    }
}

private fun AuthenticationConfig.registerSessionAuthentication(authenticationDataSource: AuthenticationDataSource) {
    session<Token>(SESSION_AUTHENTICATION) {
        validate { token ->
            if (authenticationDataSource.isUserSessionValid(token)) token else null
        }

        challenge {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

private fun AuthenticationConfig.registerTokenAuthentication(authenticationDataSource: AuthenticationDataSource) {
    bearer(TOKEN_AUTHENTICATION) {
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