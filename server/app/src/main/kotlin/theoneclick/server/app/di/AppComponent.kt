package theoneclick.server.app.di

import io.ktor.util.logging.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import theoneclick.server.app.dataSources.AuthenticationDataSource
import theoneclick.server.app.dataSources.DefaultAuthenticationDataSource
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.server.app.security.DefaultUuidProvider
import theoneclick.server.app.security.Encryptor
import theoneclick.server.app.security.IvGenerator
import theoneclick.server.app.security.UuidProvider
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
    val uuidProvider: UuidProvider = DefaultUuidProvider(),
) {
    abstract val authenticationDataSource: AuthenticationDataSource

    protected val DefaultAuthenticationDataSource.bind: AuthenticationDataSource
        @Provides get() = this
}

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
private annotation class AppScope