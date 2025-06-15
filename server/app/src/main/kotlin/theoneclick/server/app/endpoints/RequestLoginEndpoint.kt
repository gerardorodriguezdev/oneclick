package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.User
import theoneclick.server.app.models.UserSession
import theoneclick.server.app.models.Username
import theoneclick.server.app.platform.SecurityUtils
import theoneclick.server.app.platform.UuidProvider
import org.koin.ktor.ext.inject
import theoneclick.server.app.validators.ParamsValidator
import theoneclick.server.app.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.app.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.server.shared.extensions.agent
import theoneclick.shared.contracts.core.agents.Agent
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.dtos.requests.RequestLoginRequestDto
import theoneclick.shared.contracts.core.dtos.responses.RequestLoginResponseDto

fun Routing.requestLoginEndpoint() {
    val usersDataSource: UsersDataSource by inject()
    val securityUtils: SecurityUtils by inject()
    val paramsValidator: ParamsValidator by inject()
    val uuidProvider: UuidProvider by inject()

    post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequestDto: RequestLoginRequestDto ->
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
            call.respond(RequestLoginResponseDto(token = userSession.sessionToken))
        }

        Agent.BROWSER -> {
            call.sessions.set(userSession)
            call.respond(HttpStatusCode.OK)
        }
    }
}
