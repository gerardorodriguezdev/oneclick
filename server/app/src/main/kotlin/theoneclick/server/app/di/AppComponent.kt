package theoneclick.server.app.di

import io.ktor.server.application.*
import io.ktor.util.logging.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import theoneclick.server.shared.di.Environment
import theoneclick.server.shared.repositories.HomesRepository
import theoneclick.server.shared.repositories.UsersRepository
import theoneclick.server.shared.security.DefaultUuidProvider
import theoneclick.server.shared.security.Encryptor
import theoneclick.server.shared.security.IvGenerator
import theoneclick.server.shared.security.UuidProvider
import theoneclick.shared.timeProvider.TimeProvider

@AppScope
@Component
abstract class AppComponent(
    @get:Provides
    val environment: Environment,
    @get:Provides
    val ivGenerator: IvGenerator,
    @get:Provides
    val encryptor: Encryptor,
    @get:Provides
    val timeProvider: TimeProvider,
    @get:Provides
    val logger: Logger,
    @get:Provides
    val usersRepository: UsersRepository,
    @get:Provides
    val homesRepository: HomesRepository,
    @get:Provides
    val uuidProvider: UuidProvider = DefaultUuidProvider(),
    val onShutdown: (application: Application) -> Unit,
)

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
private annotation class AppScope
