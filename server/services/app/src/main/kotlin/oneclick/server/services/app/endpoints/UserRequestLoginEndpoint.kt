package oneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import oneclick.server.services.app.dataSources.base.UsersDataSource
import oneclick.server.services.app.dataSources.models.User
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.auth.security.PasswordManager
import oneclick.server.shared.auth.security.UserJwtProvider
import oneclick.server.shared.auth.security.UuidProvider
import oneclick.server.shared.core.clientType
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.auth.models.requests.LoginRequest.UserRequestLoginRequest
import oneclick.shared.contracts.auth.models.responses.RequestLoginResponse
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint

internal fun Routing.userRequestLoginEndpoint(
    usersRepository: UsersRepository,
    passwordManager: PasswordManager,
    uuidProvider: UuidProvider,
    userJwtProvider: UserJwtProvider,
) {
    post(ClientEndpoint.USER_REQUEST_LOGIN.route) { userRequestLoginRequest: UserRequestLoginRequest ->
        val (username, password) = userRequestLoginRequest
        val clientType = call.request.clientType
        val user = usersRepository.user(UsersDataSource.Findable.ByUsername(username))

        when {
            user == null -> registerUser(
                username = username,
                password = password,
                passwordManager = passwordManager,
                uuidProvider = uuidProvider,
                userJwtProvider = userJwtProvider,
                usersRepository = usersRepository,
                clientType = clientType,
            )

            !passwordManager.verifyPassword(
                password = password,
                hashedPassword = user.hashedPassword
            ) -> handleError()

            else -> respondJwt(jwt = userJwtProvider.jwt(user.userId), clientType = clientType)
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
        handleError()
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

        else -> handleError()
    }
}

private suspend fun RoutingContext.handleError() {
    call.respond(HttpStatusCode.BadRequest)
}
