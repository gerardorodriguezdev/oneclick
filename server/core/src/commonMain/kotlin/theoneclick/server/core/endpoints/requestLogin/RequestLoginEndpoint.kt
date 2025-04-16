package theoneclick.server.core.endpoints.requestLogin

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.post
import theoneclick.server.core.models.User
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.server.core.platform.UuidProvider
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.validators.ParamsValidator
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.server.shared.extensions.agent
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.RequestLoginResponse

fun Routing.requestLoginEndpoint() {
    val userDataSource: UserDataSource by inject()
    val securityUtils: SecurityUtils by inject()
    val paramsValidator: ParamsValidator by inject()
    val uuidProvider: UuidProvider by inject()

    post(
        endpoint = ClientEndpoint.REQUEST_LOGIN,
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
    val user = validRequestLogin.userData(uuidProvider, securityUtils)

    val sessionToken = securityUtils.encryptedToken()
    userDataSource.saveUser(
        user.copy(sessionToken = sessionToken)
    )

    val userSession = UserSession(sessionToken = sessionToken.value)
    handleSuccess(userSession)
}

private fun ValidRequestLogin.userData(
    uuidProvider: UuidProvider,
    securityUtils: SecurityUtils
): User =
    when (this) {
        is ValidRequestLogin.ValidUser -> user
        is ValidRequestLogin.RegistrableUser -> {
            User(
                id = uuidProvider.uuid(),
                username = username,
                hashedPassword = securityUtils.hashPassword(password),
            )
        }
    }

private suspend fun RoutingContext.handleSuccess(userSession: UserSession) {
    when (call.request.agent) {
        Agent.MOBILE -> {
            call.respond(RequestLoginResponse(userSession.sessionToken))
        }

        Agent.BROWSER -> {
            call.sessions.set(userSession)
            call.respond(HttpStatusCode.OK)
        }
    }
}
