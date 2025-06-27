package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.extensions.agent
import theoneclick.server.shared.models.User
import theoneclick.server.shared.repositories.SessionsRepository
import theoneclick.server.shared.repositories.UsersRepository
import theoneclick.server.shared.security.Encryptor
import theoneclick.server.shared.security.UuidProvider
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.models.requests.RequestLoginRequest
import theoneclick.shared.contracts.core.models.responses.RequestLoginResponse

fun Routing.requestLoginEndpoint(
    usersRepository: UsersRepository,
    sessionsRepository: SessionsRepository,
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
                sessionsRepository = sessionsRepository,
            )

            !encryptor.verifyPassword(
                password = password,
                hashedPassword = user.hashedPassword
            ) -> handleError()

            else -> saveSession(
                userId = user.userId,
                encryptor = encryptor,
                sessionsRepository = sessionsRepository,
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
    sessionsRepository: SessionsRepository,
) {
    val newUser = User(
        userId = uuidProvider.uuid(),
        username = username,
        hashedPassword = encryptor.hashPassword(password),
    )
    usersRepository.saveUser(newUser)

    saveSession(
        userId = newUser.userId,
        encryptor = encryptor,
        sessionsRepository = sessionsRepository,
    )
}

private suspend fun RoutingContext.saveSession(
    userId: Uuid,
    encryptor: Encryptor,
    sessionsRepository: SessionsRepository,
) {
    val sessionToken = encryptor.encryptedToken()
    sessionsRepository.saveSession(userId, sessionToken)

    respondToken(sessionToken.token)
}

private suspend fun RoutingContext.respondToken(token: Token) {
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
