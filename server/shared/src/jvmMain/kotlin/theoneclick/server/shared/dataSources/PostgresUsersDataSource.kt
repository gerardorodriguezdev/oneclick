package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import kotlinx.coroutines.withContext
import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.HashedPassword
import theoneclick.server.shared.models.User
import theoneclick.server.shared.postgresql.SharedDatabase
import theoneclick.server.shared.postgresql.Users
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class PostgresUsersDataSource(
    private val database: SharedDatabase,
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
        } catch (e: Exception) {
            logger.error("Error trying to find user", e)
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
        } catch (e: Exception) {
            logger.error("Error trying to save user", e)
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
