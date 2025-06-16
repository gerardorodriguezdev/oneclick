package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.UserDto
import theoneclick.server.app.security.Encryptor
import theoneclick.server.app.security.UuidProvider
import theoneclick.server.shared.extensions.agent
import theoneclick.shared.contracts.core.agents.Agent
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.UsernameDto
import theoneclick.shared.contracts.core.dtos.requests.RequestLoginRequestDto
import theoneclick.shared.contracts.core.dtos.responses.RequestLoginResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.requestLoginEndpoint(
    usersDataSource: UsersDataSource,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) {
    post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequestDto: RequestLoginRequestDto ->
        val username = requestLoginRequestDto.username
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
    username: UsernameDto,
    password: String,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
    usersDataSource: UsersDataSource,
) {
    val newUser = UserDto(
        id = uuidProvider.uuid(),
        username = username,
        hashedPassword = encryptor.hashPassword(password),
        sessionToken = null,
        homes = emptyList(),
    )

    handleSuccess(
        user = newUser,
        encryptor = encryptor,
        usersDataSource = usersDataSource,
    )
}

private suspend fun RoutingContext.handleSuccess(
    user: UserDto,
    encryptor: Encryptor,
    usersDataSource: UsersDataSource,
) {
    val sessionToken = encryptor.encryptedToken()
    usersDataSource.saveUser(
        user.copy(sessionToken = sessionToken)
    )

    handleSuccess(sessionToken.token)
}

private suspend fun RoutingContext.handleSuccess(token: TokenDto) {
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
