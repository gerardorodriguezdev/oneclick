package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.app.dataSources.base.UsersDataSource
import theoneclick.server.app.models.User
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.server.app.security.Encryptor
import theoneclick.server.app.security.UuidProvider
import theoneclick.server.shared.extensions.agent
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.requests.RequestLoginRequest
import theoneclick.shared.contracts.core.models.responses.RequestLoginResponse
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

fun Routing.requestLoginEndpoint(
    usersRepository: UsersRepository,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) {
    post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequest: RequestLoginRequest ->
        val username = requestLoginRequest.username
        val password = requestLoginRequest.password.value
        val user = usersRepository.user(UsersDataSource.Findable.ByUsername(username))

        when {
            user == null -> registerUser(
                username = username,
                password = password,
                encryptor = encryptor,
                uuidProvider = uuidProvider,
                usersRepository = usersRepository,
            )

            !encryptor.verifyPassword(
                password = password,
                hashedPassword = user.hashedPassword
            ) -> handleError()

            else -> handleSuccess(
                user = user,
                encryptor = encryptor,
                usersRepository = usersRepository,
            )
        }
    }
}

private suspend fun RoutingContext.registerUser(
    username: Username,
    password: String,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
    usersRepository: UsersRepository,
) {
    val newUser = User(
        userId = uuidProvider.uuid(),
        username = username,
        hashedPassword = encryptor.hashPassword(password),
        sessionToken = null,
    )

    handleSuccess(
        user = newUser,
        encryptor = encryptor,
        usersRepository = usersRepository,
    )
}

private suspend fun RoutingContext.handleSuccess(
    user: User,
    encryptor: Encryptor,
    usersRepository: UsersRepository,
) {
    val sessionToken = encryptor.encryptedToken()
    usersRepository.saveUser(
        user.copy(sessionToken = sessionToken)
    )

    handleSuccess(sessionToken.token)
}

private suspend fun RoutingContext.handleSuccess(token: Token) {
    when (call.request.agent) {
        Agent.MOBILE -> {
            call.respond(
                RequestLoginResponse(
                    token = token
                )
            )
        }

        Agent.BROWSER -> {
            call.sessions.set(token)
            call.respond(HttpStatusCode.OK)
        }
    }
}

private suspend fun RoutingContext.handleError() {
    call.respond(HttpStatusCode.BadRequest)
}
