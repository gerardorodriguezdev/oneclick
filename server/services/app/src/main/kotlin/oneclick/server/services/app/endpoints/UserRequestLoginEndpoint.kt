package oneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import oneclick.server.services.app.dataSources.base.UsersDataSource
import oneclick.server.services.app.dataSources.models.User
import oneclick.server.services.app.plugins.apiRateLimit
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.authentication.security.PasswordManager
import oneclick.server.services.app.authentication.UserJwtProvider
import oneclick.server.shared.authentication.security.UuidProvider
import oneclick.server.shared.utils.clientType
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.auth.models.requests.LoginRequest.UserRequestLoginRequest
import oneclick.shared.contracts.auth.models.responses.RequestLoginResponse
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.contracts.core.models.ClientEndpoint

internal fun Routing.userRequestLoginEndpoint(
    usersRepository: UsersRepository,
    passwordManager: PasswordManager,
    uuidProvider: UuidProvider,
    userJwtProvider: UserJwtProvider,
) {
    apiRateLimit {
        post(ClientEndpoint.USER_REQUEST_LOGIN.route) { userRequestLoginRequest: UserRequestLoginRequest ->
            val (username, password) = userRequestLoginRequest

            val clientType = call.request.clientType
            if (clientType == ClientType.DESKTOP) {
                respondInvalidClientType()
                return@post
            }

            val user = usersRepository.user(UsersDataSource.Findable.ByUsername(username))

            when {
                user == null -> {
                    call.application.log.debug("Registrable user")
                    registerUser(
                        username = username,
                        password = password,
                        passwordManager = passwordManager,
                        uuidProvider = uuidProvider,
                        userJwtProvider = userJwtProvider,
                        usersRepository = usersRepository,
                        clientType = clientType,
                    )
                }

                !passwordManager.verifyPassword(
                    password = password,
                    hashedPassword = user.hashedPassword
                ) -> {
                    call.application.log.debug("Invalid password")
                    call.respond(HttpStatusCode.Unauthorized)
                }

                else -> respondJwt(jwt = userJwtProvider.jwt(user.userId), clientType = clientType)
            }
        }
    }
}

private suspend fun RoutingContext.registerUser(
    username: Username,
    password: Password,
    clientType: ClientType,
    passwordManager: PasswordManager,
    userJwtProvider: UserJwtProvider,
    uuidProvider: UuidProvider,
    usersRepository: UsersRepository,
) {
    val newUser = User(
        userId = uuidProvider.uuid(),
        username = username,
        hashedPassword = passwordManager.hashPassword(password),
    )

    val isUserSaved = usersRepository.saveUser(newUser)
    if (!isUserSaved) {
        call.application.log.debug("User not saved")
        call.respond(HttpStatusCode.InternalServerError)
        return
    }

    val jwt = userJwtProvider.jwt(newUser.userId)
    respondJwt(jwt = jwt, clientType = clientType)
}

private suspend fun RoutingContext.respondJwt(jwt: Jwt, clientType: ClientType) {
    when (clientType) {
        ClientType.MOBILE -> {
            call.respond(
                RequestLoginResponse(jwt = jwt)
            )
        }

        ClientType.BROWSER -> {
            call.sessions.set(jwt)
            call.respond(HttpStatusCode.OK)
        }

        else -> respondInvalidClientType()
    }
}

private suspend fun RoutingContext.respondInvalidClientType() {
    call.application.log.debug("Invalid client type")
    call.respond(HttpStatusCode.BadRequest)
}