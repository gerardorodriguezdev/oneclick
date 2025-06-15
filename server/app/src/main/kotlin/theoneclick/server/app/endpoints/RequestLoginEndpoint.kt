package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.Token
import theoneclick.server.app.models.Token.Companion.toToken
import theoneclick.server.app.models.User
import theoneclick.server.app.models.Username
import theoneclick.server.app.models.Username.Companion.toUsername
import theoneclick.server.app.security.Encryptor
import theoneclick.server.app.security.UuidProvider
import theoneclick.server.shared.extensions.agent
import theoneclick.shared.contracts.core.agents.Agent
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.requests.RequestLoginRequestDto
import theoneclick.shared.contracts.core.dtos.responses.RequestLoginResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.requestLoginEndpoint() {
    val usersDataSource: UsersDataSource by inject()
    val encryptor: Encryptor by inject()
    val uuidProvider: UuidProvider by inject()

    post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequestDto: RequestLoginRequestDto ->
        val username = requestLoginRequestDto.username.toUsername()
        val password = requestLoginRequestDto.password.value
        val user = usersDataSource.user(username = username)

        when {
            user == null ->
                registerUser(
                    username = username,
                    password = password,
                    encryptor = encryptor,
                    uuidProvider = uuidProvider,
                    usersDataSource = usersDataSource,
                )

            !encryptor.verifyPassword(
                password = password,
                hashedPassword = user.hashedPassword
            ) -> handleError()

            else -> handleSuccess(
                user = user,
                encryptor = encryptor,
                usersDataSource = usersDataSource,
            )
        }
    }
}

private suspend fun RoutingContext.registerUser(
    username: Username,
    password: String,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
    usersDataSource: UsersDataSource,
) {
    val newUser = User(
        id = uuidProvider.uuid(),
        username = username,
        hashedPassword = encryptor.hashPassword(password),
        sessionToken = null,
    )

    handleSuccess(
        user = newUser,
        encryptor = encryptor,
        usersDataSource = usersDataSource,
    )
}

private suspend fun RoutingContext.handleSuccess(
    user: User,
    encryptor: Encryptor,
    usersDataSource: UsersDataSource,
) {
    val sessionToken = encryptor.encryptedToken()
    usersDataSource.saveUser(
        user.copy(sessionToken = sessionToken)
    )

    val token = sessionToken.toToken()
    handleSuccess(token)
}

private suspend fun RoutingContext.handleSuccess(token: Token) {
    when (call.request.agent) {
        Agent.MOBILE -> {
            call.respond(
                RequestLoginResponseDto(
                    token = TokenDto.unsafe(token.value)
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
