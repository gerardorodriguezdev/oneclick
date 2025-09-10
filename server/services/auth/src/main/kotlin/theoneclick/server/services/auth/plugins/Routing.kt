package theoneclick.server.services.auth.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.services.auth.endpoints.isUserLoggedEndpoint
import theoneclick.server.services.auth.endpoints.logoutEndpoint
import theoneclick.server.services.auth.endpoints.requestLoginEndpoint
import theoneclick.server.services.auth.repositories.UsersRepository
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.server.shared.auth.security.UuidProvider

fun Application.configureRouting(
    usersRepository: UsersRepository,
    encryptor: Encryptor,
    jwtProvider: JwtProvider,
    uuidProvider: UuidProvider,
) {
    routing {
        requestLoginEndpoint(
            usersRepository = usersRepository,
            encryptor = encryptor,
            jwtProvider = jwtProvider,
            uuidProvider = uuidProvider,
        )
        isUserLoggedEndpoint()
        logoutEndpoint()
    }
}
