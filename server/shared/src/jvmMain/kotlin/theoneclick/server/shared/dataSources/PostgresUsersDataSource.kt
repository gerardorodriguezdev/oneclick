package theoneclick.server.shared.dataSources

import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.HashedPassword
import theoneclick.server.shared.models.User
import theoneclick.server.shared.postgresql.Users
import theoneclick.server.shared.postgresql.UsersDatabase
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.Uuid

class PostgresUsersDataSource(private val database: UsersDatabase) : UsersDataSource {

    override fun user(findable: UsersDataSource.Findable): User? =
        when (findable) {
            is UsersDataSource.Findable.ByUserId -> {
                val dbUser = database.usersQueries.userByUserId(findable.userId.value).executeAsOneOrNull()
                dbUser?.let {
                    User(
                        userId = Uuid.unsafe(dbUser.user_id),
                        username = Username.unsafe(dbUser.username),
                        hashedPassword = HashedPassword.unsafe(dbUser.hashed_password)
                    )
                }
            }

            is UsersDataSource.Findable.ByUsername -> {
                val dbUser = database.usersQueries.userByUsername(findable.username.value).executeAsOneOrNull()
                dbUser?.let {
                    User(
                        userId = Uuid.unsafe(dbUser.user_id),
                        username = Username.unsafe(dbUser.username),
                        hashedPassword = HashedPassword.unsafe(dbUser.hashed_password)
                    )
                }
            }
        }

    override fun saveUser(user: User) {
        database.usersQueries.insertUser(
            Users(
                user_id = user.userId.value,
                username = user.username.value,
                hashed_password = user.hashedPassword.value,
            )
        )
    }
}