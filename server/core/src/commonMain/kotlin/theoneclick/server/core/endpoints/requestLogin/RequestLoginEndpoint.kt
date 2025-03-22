package theoneclick.server.core.endpoints.requestLogin

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.post
import theoneclick.server.core.models.UserData
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.server.core.platform.UuidProvider
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.validators.ParamsValidator
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.shared.core.models.endpoints.ClientEndpoints
import theoneclick.shared.core.models.requests.RequestLoginRequest

fun Routing.requestLoginEndpoint() {
    val userDataSource: UserDataSource by inject()
    val securityUtils: SecurityUtils by inject()
    val paramsValidator: ParamsValidator by inject()
    val uuidProvider: UuidProvider by inject()

    post(
        endpoint = ClientEndpoints.REQUEST_LOGIN,
        paramsParsing = {
            val requestLoginRequest: RequestLoginRequest = call.receive()
            RequestLoginParams(
                username = requestLoginRequest.username,
                password = requestLoginRequest.password,
            )
        },
        paramsValidation = { requestLoginParams -> paramsValidator.isRequestLoginParamsValid(requestLoginParams) }
    ) { requestLoginValidationResult ->
        when (requestLoginValidationResult) {
            is ValidRequestLogin -> {
                handleSuccess(
                    validRequestLogin = requestLoginValidationResult,
                    securityUtils = securityUtils,
                    userDataSource = userDataSource,
                    uuidProvider = uuidProvider,
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
    uuidProvider: UuidProvider,
) {
    val userData = validRequestLogin.userData(uuidProvider, securityUtils)

    val sessionToken = securityUtils.encryptedToken()
    userDataSource.saveUserData(
        userData.copy(sessionToken = sessionToken)
    )

    call.sessions.set(UserSession(sessionToken = sessionToken.value))
    call.respond(HttpStatusCode.OK)
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