package theoneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.*
import theoneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import theoneclick.server.services.app.endpoints.*
import theoneclick.server.services.app.repositories.HomesRepository
import theoneclick.server.services.app.repositories.UsersRepository
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.server.shared.auth.security.UuidProvider

internal fun Application.configureRouting(
    usersRepository: UsersRepository,
    encryptor: Encryptor,
    jwtProvider: JwtProvider,
    uuidProvider: UuidProvider,
    homesRepository: HomesRepository,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    routing {
        healthzEndpoint()
        isUserLoggedEndpoint()
        logoutEndpoint(invalidJwtDataSource = invalidJwtDataSource)
        requestLoginEndpoint(
            usersRepository = usersRepository,
            encryptor = encryptor,
            jwtProvider = jwtProvider,
            uuidProvider = uuidProvider,
        )
        homesListEndpoint(homesRepository = homesRepository)
        staticResources("/", "static")
    }
}