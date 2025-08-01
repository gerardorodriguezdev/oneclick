package theoneclick.server.shared.dataSources

import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.User
import theoneclick.shared.contracts.core.models.Uuid
import java.util.concurrent.ConcurrentHashMap

class MemoryUsersDataSource : UsersDataSource {
    private val users = ConcurrentHashMap<Uuid, User>()

    override suspend fun user(findable: UsersDataSource.Findable): User? =
        when (findable) {
            is UsersDataSource.Findable.ByUserId -> users[findable.userId]

            is UsersDataSource.Findable.ByUsername ->
                users.values.firstOrNull { user -> user.username == findable.username }
        }

    override suspend fun saveUser(user: User): Boolean {
        val currentSize = users.size

        if (currentSize > CLEAN_UP_LIMIT) {
            users.clear()
        }

        users[user.userId] = user
        return true
    }

    private companion object {
        const val CLEAN_UP_LIMIT = 10_000
    }
}
