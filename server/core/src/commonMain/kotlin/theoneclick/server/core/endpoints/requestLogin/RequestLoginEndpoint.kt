package theoneclick.server.core.endpoints.requestLogin

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.data.models.UserData
import theoneclick.server.core.data.models.UserSession
import theoneclick.server.core.data.validators.ParamsValidator
import theoneclick.server.core.data.validators.ParamsValidator.AuthorizeValidationResult
import theoneclick.server.core.data.validators.ParamsValidator.AuthorizeValidationResult.InvalidAuthorizeParams
import theoneclick.server.core.data.validators.ParamsValidator.AuthorizeValidationResult.ValidAuthorizeParams
import theoneclick.server.core.data.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.core.data.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.endpoints.authorize.AuthorizeParams
import theoneclick.server.core.extensions.post
import theoneclick.server.core.platform.Environment
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.server.core.platform.UuidProvider
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.models.endpoints.Endpoint
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.RequestLoginResponse
import theoneclick.shared.core.extensions.urlBuilder
import theoneclick.shared.core.models.routes.AppRoute

fun Routing.requestLoginEndpoint() {
    val userDataSource: UserDataSource by inject()
    val securityUtils: SecurityUtils by inject()
    val environment: Environment by inject()
    val paramsValidator: ParamsValidator by inject()
    val uuidProvider: UuidProvider by inject()

    post(
        endpoint = Endpoint.REQUEST_LOGIN,
        paramsParsing = {
            val requestLoginRequest: RequestLoginRequest = call.receive()
            RequestLoginParams(
                username = requestLoginRequest.username,
                password = requestLoginRequest.password,
                authorizeParams = call.sessions.get(),
            )
        },
        paramsValidation = { requestLoginParams -> paramsValidator.isRequestLoginParamsValid(requestLoginParams) }
    ) { requestLoginValidationResult ->
        when (requestLoginValidationResult) {
            is ValidRequestLogin -> {
                handleSuccess(
                    requestLoginValidationResult,
                    securityUtils,
                    userDataSource,
                    environment,
                    uuidProvider,
                )
            }

            is InvalidRequestLoginParams -> call.respond(HttpStatusCode.BadRequest)
        }
    }
}

private suspend fun RoutingContext.handleSuccess(
    validRequestLogin: ValidRequestLogin,
    securityUtils: SecurityUtils,
    userDataSource: UserDataSource,
    environment: Environment,
    uuidProvider: UuidProvider,
) {
    val userData = validRequestLogin.userData(uuidProvider, securityUtils)

    val sessionToken = securityUtils.encryptedToken()
    userDataSource.saveUserData(
        userData.copy(
            sessionToken = sessionToken,
            authorizationCode = null,
            state = null,
            accessToken = null,
            refreshToken = null,
        )
    )

    call.sessions.clear<AuthorizeParams>()
    call.sessions.set(UserSession(sessionToken = sessionToken.value))

    handleAuthorizeParams(validRequestLogin.authorizeValidationResult, environment)
}

private fun ValidRequestLogin.userData(
    uuidProvider: UuidProvider,
    securityUtils: SecurityUtils
): UserData =
    when (this) {
        is ValidRequestLogin.ValidUser -> userData
        is ValidRequestLogin.RegistrableUser -> {
            UserData(
                userId = uuidProvider.uuid(),
                username = username,
                hashedPassword = securityUtils.hashPassword(password),
            )
        }
    }

private suspend fun RoutingContext.handleAuthorizeParams(
    authorizeValidationResult: AuthorizeValidationResult,
    environment: Environment,
) {
    when (authorizeValidationResult) {
        is ValidAuthorizeParams -> {
            val authorizeParamsUrlBuilder = urlBuilder {
                protocol = URLProtocol.HTTPS
                host = environment.host
                path(Endpoint.AUTHORIZE.route)
                parameters.appendParameters(authorizeValidationResult)
            }

            call.respond<RequestLoginResponse>(
                RequestLoginResponse.ExternalRedirect(urlString = authorizeParamsUrlBuilder.buildString()),
            )
        }

        is InvalidAuthorizeParams -> call.respond<RequestLoginResponse>(
            RequestLoginResponse.LocalRedirect(AppRoute.Home)
        )
    }
}

private fun ParametersBuilder.appendParameters(authorizeValidationSuccess: ValidAuthorizeParams) {
    append("state", authorizeValidationSuccess.state)
    append("client_id", authorizeValidationSuccess.clientId)
    append("redirect_uri", authorizeValidationSuccess.redirectUri)
    append("response_type", authorizeValidationSuccess.responseType)
}
