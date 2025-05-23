package theoneclick.server.core.endpoints.requestLogin

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.dataSources.UsersDataSource
import theoneclick.server.core.models.User
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.models.Username
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
    val usersDataSource: UsersDataSource by inject()
    val securityUtils: SecurityUtils by inject()
    val paramsValidator: ParamsValidator by inject()
    val uuidProvider: UuidProvider by inject()

    post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequest: RequestLoginRequest ->
        val requestLoginParams = RequestLoginParams(
            username = requestLoginRequest.username,
            password = requestLoginRequest.password,
        )
        val requestLoginValidationResult = paramsValidator.isRequestLoginParamsValid(requestLoginParams)

        when (requestLoginValidationResult) {
            is ValidRequestLogin -> {
                handleSuccess(
                    validRequestLogin = requestLoginValidationResult,
                    securityUtils = securityUtils,
                    usersDataSource = usersDataSource,
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
    usersDataSource: UsersDataSource,
    uuidProvider: UuidProvider,
) {
    val user = validRequestLogin.user(uuidProvider, securityUtils)

    val sessionToken = securityUtils.encryptedToken()
    usersDataSource.saveUser(
        user.copy(sessionToken = sessionToken)
    )

    val userSession = UserSession(sessionToken = sessionToken.value)
    handleSuccess(userSession)
}

private fun ValidRequestLogin.user(
    uuidProvider: UuidProvider,
    securityUtils: SecurityUtils
): User =
    when (this) {
        is ValidRequestLogin.ValidUser -> user
        is ValidRequestLogin.RegistrableUser -> {
            User(
                id = uuidProvider.uuid(),
                username = Username(username),
                hashedPassword = securityUtils.hashPassword(password),
            )
        }
    }

private suspend fun RoutingContext.handleSuccess(userSession: UserSession) {
    when (call.request.agent) {
        Agent.MOBILE -> {
            call.respond(RequestLoginResponse(token = userSession.sessionToken))
        }

        Agent.BROWSER -> {
            call.sessions.set(userSession)
            call.respond(HttpStatusCode.OK)
        }
    }
}
