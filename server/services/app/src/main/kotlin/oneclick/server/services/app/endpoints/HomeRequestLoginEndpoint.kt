package oneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oneclick.server.services.app.dataSources.base.UsersDataSource
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.authentication.security.HomeJwtProvider
import oneclick.server.shared.authentication.security.PasswordManager
import oneclick.server.shared.utils.clientType
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.auth.models.requests.LoginRequest.HomeRequestLoginRequest
import oneclick.shared.contracts.auth.models.responses.RequestLoginResponse
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import oneclick.shared.contracts.homes.models.Home

internal fun Routing.homeRequestLoginEndpoint(
    usersRepository: UsersRepository,
    homesRepository: HomesRepository,
    passwordManager: PasswordManager,
    homeJwtProvider: HomeJwtProvider,
) {
    post(ClientEndpoint.HOME_REQUEST_LOGIN.route) { homeRequestLoginRequest: HomeRequestLoginRequest ->
        val (username, password, homeId) = homeRequestLoginRequest
        val clientType = call.request.clientType
        val user = usersRepository.user(UsersDataSource.Findable.ByUsername(username))
        if (user == null) {
            application.log.debug("User not found")
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val home = homesRepository.home(userId = user.userId, homeId = homeId)

        when {
            !passwordManager.verifyPassword(
                password = password,
                hashedPassword = user.hashedPassword
            ) -> {
                application.log.debug("Invalid password")
                call.respond(HttpStatusCode.Unauthorized)
            }

            home == null -> {
                application.log.debug("Registrable home")
                registerHome(
                    userId = user.userId,
                    clientType = clientType,
                    homesRepository = homesRepository,
                    homeJwtProvider = homeJwtProvider,
                    homeId = homeId,
                )
            }

            else -> {
                respondJwt(
                    jwt = homeJwtProvider.jwt(userId = user.userId, homeId = home.id),
                    clientType = clientType
                )
            }
        }
    }
}

private suspend fun RoutingContext.registerHome(
    userId: Uuid,
    clientType: ClientType,
    homesRepository: HomesRepository,
    homeJwtProvider: HomeJwtProvider,
    homeId: Uuid,
) {
    val newHome = Home(
        id = homeId,
        devices = UniqueList.emptyUniqueList(),
    )

    val isHomeSaved = homesRepository.saveHome(userId = userId, home = newHome)
    if (!isHomeSaved) {
        call.application.log.debug("Home not saved")
        call.respond(HttpStatusCode.InternalServerError)
        return
    }

    val jwt = homeJwtProvider.jwt(userId = userId, homeId = newHome.id)
    respondJwt(jwt = jwt, clientType = clientType)
}

private suspend fun RoutingContext.respondJwt(jwt: Jwt, clientType: ClientType) {
    when (clientType) {
        ClientType.DESKTOP -> {
            call.respond(
                RequestLoginResponse(jwt = jwt)
            )
        }

        else -> {
            call.application.log.debug("Invalid client")
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
