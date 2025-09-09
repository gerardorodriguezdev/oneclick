package theoneclick.server.services.auth.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.services.auth.dataSources.base.UsersDataSource
import theoneclick.server.shared.extensions.agent
import theoneclick.server.shared.models.JwtPayload
import theoneclick.server.shared.models.User
import theoneclick.server.services.auth.repositories.UsersRepository
import theoneclick.server.shared.security.Encryptor
import theoneclick.server.shared.security.UuidProvider
import theoneclick.shared.contracts.core.models.Jwt
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.models.requests.RequestLoginRequest
import theoneclick.shared.contracts.core.models.responses.RequestLoginResponse

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

            else -> createJwt(
                userId = user.userId,
                encryptor = encryptor,
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
    )
    usersRepository.saveUser(newUser)

    createJwt(
        userId = newUser.userId,
        encryptor = encryptor,
    )
}

private suspend fun RoutingContext.createJwt(
    userId: Uuid,
    encryptor: Encryptor,
) {
    val jwtPayload = JwtPayload(userId)
    val jwt = encryptor.jwt(jwtPayload)
    respondJwt(jwt)
}

private suspend fun RoutingContext.respondJwt(jwt: Jwt) {
    when (call.request.agent) {
        Agent.MOBILE -> {
            call.respond(
                RequestLoginResponse(jwt = jwt)
            )
        }

        Agent.BROWSER -> {
            call.sessions.set(jwt)
            call.respond(HttpStatusCode.OK)
        }
    }
}

private suspend fun RoutingContext.handleError() {
    call.respond(HttpStatusCode.BadRequest)
}
