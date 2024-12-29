package theoneclick.server.core.endpoints.authorize

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.data.models.GoogleHomeActionsRedirectUrl
import theoneclick.server.core.data.validators.ParamsValidator
import theoneclick.server.core.data.validators.ParamsValidator.AuthorizeValidationResult.InvalidAuthorizeParams
import theoneclick.server.core.data.validators.ParamsValidator.AuthorizeValidationResult.ValidAuthorizeParams
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.*
import theoneclick.server.core.extensions.responseType
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.dataSources.models.endpoints.Endpoint

fun Routing.authorizeEndpoint() {
    val securityUtils: SecurityUtils by inject()
    val userDataSource: UserDataSource by inject()
    val paramsValidator: ParamsValidator by inject()

    userSessionAuthentication {
        get(
            endpoint = Endpoint.AUTHORIZE,
            paramsParsing = { call.queryParameters.authorizeParams() },
            paramsValidation = paramsValidator::isAuthorizeParamsValid,
        ) { authorizeValidationResult ->
            when (authorizeValidationResult) {
                is ValidAuthorizeParams -> handleValidAuthorizeParams(
                    securityUtils,
                    userDataSource,
                    authorizeValidationResult,
                )

                is InvalidAuthorizeParams -> call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

private suspend fun RoutingContext.handleValidAuthorizeParams(
    securityUtils: SecurityUtils,
    userDataSource: UserDataSource,
    validAuthorizeParams: ValidAuthorizeParams,
) {
    when (validAuthorizeParams) {
        is ValidAuthorizeParams.WithUserData -> handleValidAuthorizeParamsWithUserData(
            securityUtils,
            userDataSource,
            validAuthorizeParams,
        )

        is ValidAuthorizeParams.WithoutUserData -> call.respond(HttpStatusCode.InternalServerError)
    }
}

private suspend fun RoutingContext.handleValidAuthorizeParamsWithUserData(
    securityUtils: SecurityUtils,
    userDataSource: UserDataSource,
    validAuthorizeParams: ValidAuthorizeParams.WithUserData,
) {
    val authorizationCode = securityUtils.encryptedToken()

    userDataSource.saveUserData(
        validAuthorizeParams.userData.copy(
            authorizationCode = authorizationCode,
            state = validAuthorizeParams.state,
            accessToken = null,
            refreshToken = null,
        )
    )

    call.respondRedirect(
        GoogleHomeActionsRedirectUrl.create(
            clientId = validAuthorizeParams.clientId,
            authorizationCode = authorizationCode.value,
            state = validAuthorizeParams.state,
        ).value
    )
}

private fun Parameters.authorizeParams(): AuthorizeParams =
    AuthorizeParams(
        clientId = clientId,
        state = state,
        redirectUri = redirectUri,
        responseType = responseType,
    )
