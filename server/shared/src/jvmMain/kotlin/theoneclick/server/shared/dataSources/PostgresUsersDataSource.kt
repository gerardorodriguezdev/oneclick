package theoneclick.server.shared.dataSources

import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.User
import theoneclick.server.shared.postgresql.Users
import theoneclick.server.shared.postgresql.UsersDatabase

class PostgresUsersDataSource(
    private val database: UsersDatabase,
) : UsersDataSource {
    override fun user(findable: UsersDataSource.Findable): User? {
        TODO("Not yet implemented")
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