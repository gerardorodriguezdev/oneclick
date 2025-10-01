package theoneclick.server.services.app.dataSources

import io.ktor.util.logging.*
import kotlinx.coroutines.withContext
import migrations.Users
import theoneclick.server.services.app.dataSources.base.UsersDataSource
import theoneclick.server.services.app.dataSources.models.User
import theoneclick.server.services.app.postgresql.AppDatabase
import theoneclick.server.shared.auth.models.HashedPassword
import theoneclick.shared.contracts.auth.models.Username
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.dispatchers.platform.DispatchersProvider

internal class PostgresUsersDataSource(
    private val database: AppDatabase,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : UsersDataSource {

    override suspend fun user(findable: UsersDataSource.Findable): User? =
        try {
            withContext(dispatchersProvider.io()) {
                val dbUser = when (findable) {
                    is UsersDataSource.Findable.ByUserId -> {
                        database.usersQueries.userByUserId(findable.userId.value)
                            .executeAsOneOrNull()
                    }

                    is UsersDataSource.Findable.ByUsername -> {
                        database.usersQueries.userByUsername(findable.username.value)
                            .executeAsOneOrNull()
                    }
                }

                dbUser?.toUser()
            }
        } catch (error: Exception) {
            logger.error("Error trying to find user", error)
            null
        }

    private fun Users.toUser(): User =
        User(
            userId = Uuid.unsafe(user_id),
            username = Username.unsafe(username),
            hashedPassword = HashedPassword.unsafe(hashed_password)
        )

    override suspend fun saveUser(user: User): Boolean =
        try {
            database.usersQueries.insertUser(user.toUsers())
            true
        } catch (error: Exception) {
            logger.error("Error trying to save user", error)
            false
        }

    private companion object {
        fun User.toUsers(): Users =
            Users(
                user_id = userId.value,
                username = username.value,
                hashed_password = hashedPassword.value,
            )
    }
}
