package theoneclick.server.services.auth.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.services.auth.dataSources.base.UsersDataSource
import theoneclick.server.services.auth.dataSources.models.User
import theoneclick.server.services.auth.repositories.UsersRepository
import theoneclick.server.shared.auth.models.JwtPayload
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.server.shared.auth.security.UuidProvider
import theoneclick.server.shared.core.extensions.agent
import theoneclick.shared.contracts.auth.models.Jwt
import theoneclick.shared.contracts.auth.models.Username
import theoneclick.shared.contracts.auth.models.requests.RequestLoginRequest
import theoneclick.shared.contracts.auth.models.responses.RequestLoginResponse
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

fun Routing.requestLoginEndpoint(
    usersRepository: UsersRepository,
    encryptor: Encryptor,
    jwtProvider: JwtProvider,
    uuidProvider: UuidProvider,
) {
    post(ClientEndpoint.REQUEST_LOGIN) { requestLoginRequest: RequestLoginRequest ->
        val username = requestLoginRequest.username
        val password = requestLoginRequest.password.value
        val user = usersRepository.user(UsersDataSource.Findable.ByUsername(username))

        when {
            user == null -> registerUser(
                username = username,
                password = password,
                encryptor = encryptor,
                jwtProvider = jwtProvider,
                uuidProvider = uuidProvider,
                usersRepository = usersRepository,
            )

            !encryptor.verifyPassword(
                password = password,
                hashedPassword = user.hashedPassword
            ) -> handleError()

            else -> createJwt(
                userId = user.userId,
                jwtProvider = jwtProvider,
            )
        }
    }
}

private suspend fun RoutingContext.registerUser(
    username: Username,
    password: String,
    encryptor: Encryptor,
    jwtProvider: JwtProvider,
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
        jwtProvider = jwtProvider,
    )
}

private suspend fun RoutingContext.createJwt(
    userId: Uuid,
    jwtProvider: JwtProvider,
) {
    val jwtPayload = JwtPayload(userId)
    val jwt = jwtProvider.jwt(jwtPayload)
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
