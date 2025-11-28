package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import oneclick.server.services.app.authentication.HomeJwtProvider
import oneclick.server.services.app.authentication.UserJwtProvider
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.endpoints.*
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.RegistrableUsersRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.authentication.security.PasswordManager
import oneclick.server.shared.authentication.security.RegistrationCodeProvider
import oneclick.server.shared.authentication.security.UuidProvider
import theoneclick.server.shared.email.base.EmailService

internal fun Application.configureRouting(
    usersRepository: UsersRepository,
    passwordManager: PasswordManager,
    userJwtProvider: UserJwtProvider,
    homeJwtProvider: HomeJwtProvider,
    uuidProvider: UuidProvider,
    homesRepository: HomesRepository,
    invalidJwtDataSource: InvalidJwtDataSource,
    registrationCodeProvider: RegistrationCodeProvider,
    registrableUsersRepository: RegistrableUsersRepository,
    emailService: EmailService,
) {
    routing {
        healthzEndpoint()
        isLoggedEndpoint()
        logoutEndpoint(invalidJwtDataSource = invalidJwtDataSource)
        userRequestLoginEndpoint(
            usersRepository = usersRepository,
            passwordManager = passwordManager,
            userJwtProvider = userJwtProvider,
            registrationCodeProvider = registrationCodeProvider,
            registrableUsersRepository = registrableUsersRepository,
            emailService = emailService,
        )
        userApproveRegistrableUserEndpoint(
            usersRepository = usersRepository,
            uuidProvider = uuidProvider,
            registrableUsersRepository = registrableUsersRepository,
        )
        homeRequestLoginEndpoint(
            usersRepository = usersRepository,
            passwordManager = passwordManager,
            homeJwtProvider = homeJwtProvider,
            homesRepository = homesRepository,
        )
        userHomesEndpoint(homesRepository = homesRepository)
        homeSyncDevicesEndpoint(homesRepository = homesRepository)
        appEndpoint()
    }
}