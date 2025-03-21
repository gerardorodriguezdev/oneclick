package theoneclick.server.core.plugins.authentication

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.validators.ParamsValidator
import theoneclick.server.core.validators.ParamsValidator.AccessTokenValidationResult.InvalidAccessToken
import theoneclick.server.core.validators.ParamsValidator.AccessTokenValidationResult.ValidAccessToken
import theoneclick.server.core.validators.ParamsValidator.AuthorizeValidationResult.InvalidAuthorizeParams
import theoneclick.server.core.validators.ParamsValidator.AuthorizeValidationResult.ValidAuthorizeParams
import theoneclick.server.core.endpoints.authorize.AuthorizeParams
import theoneclick.server.core.extensions.clientId
import theoneclick.server.core.extensions.redirectUri
import theoneclick.server.core.extensions.responseType
import theoneclick.server.core.extensions.state
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.models.endpoints.ServerEndpoints
import theoneclick.shared.core.extensions.ifNotNull

fun Application.configureAuthentication() {
    val paramsValidator: ParamsValidator by inject()

    install(Authentication) {
        registerSessionAuthentication(paramsValidator)
        registerBearerAuthentication(paramsValidator)
    }
}

private fun AuthenticationConfig.registerBearerAuthentication(paramsValidator: ParamsValidator) {
    bearer(AuthenticationConstants.BEARER_AUTHENTICATION) {
        authenticate { bearerTokenCredential ->
            val accessTokenValidationResult = paramsValidator.isAccessTokenValid(bearerTokenCredential.token)
            when (accessTokenValidationResult) {
                is ValidAccessToken -> UserIdPrincipal(accessTokenValidationResult.userData.userId.value)
                is InvalidAccessToken -> null
            }
        }
    }
}

private fun AuthenticationConfig.registerSessionAuthentication(paramsValidator: ParamsValidator) {
    session<UserSession>(AuthenticationConstants.SESSION_AUTHENTICATION) {
        validate { userSession ->
            if (paramsValidator.isUserSessionValid(userSession)) userSession else null
        }

        challenge {
            call.sessions.clear<UserSession>()
            val authorizeParams = call.parameters.authorizeParams()
            val authorizeValidationResult = paramsValidator.isAuthorizeParamsValid(authorizeParams)

            when (authorizeValidationResult) {
                is ValidAuthorizeParams -> {
                    call.sessions.set(
                        AuthorizeParams(
                            state = authorizeValidationResult.state,
                            clientId = authorizeValidationResult.clientId,
                            redirectUri = authorizeValidationResult.redirectUri,
                            responseType = authorizeValidationResult.responseType,
                        )
                    )
                    call.respondRedirect(ServerEndpoints.LOGIN.route)
                }

                is InvalidAuthorizeParams -> call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

private fun Parameters.authorizeParams(): AuthorizeParams? {
    return ifNotNull(clientId, state, responseType, redirectUri) { clientId, state, responseType, redirectUri ->
        AuthorizeParams(
            clientId = clientId,
            state = state,
            responseType = responseType,
            redirectUri = redirectUri,
        )
    }
}
