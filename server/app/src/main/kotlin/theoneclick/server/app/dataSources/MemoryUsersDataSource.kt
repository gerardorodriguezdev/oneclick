package theoneclick.server.app.dataSources

import theoneclick.server.app.dataSources.base.UsersDataSource
import theoneclick.server.app.models.dtos.UserDto
import theoneclick.shared.contracts.core.dtos.UuidDto
import java.util.concurrent.ConcurrentHashMap

class MemoryUsersDataSource : UsersDataSource {
    private val users = ConcurrentHashMap<UuidDto, UserDto>()

    override fun user(findable: UsersDataSource.Findable): UserDto? =
        when (findable) {
            is UsersDataSource.Findable.ByUserId -> users[findable.userId]

            is UsersDataSource.Findable.ByToken ->
                users.values.firstOrNull { user -> user.sessionToken?.token?.value == findable.token.value }

            is UsersDataSource.Findable.ByUsername ->
                users.values.firstOrNull { user -> user.username.value == findable.username.value }
        }

    override fun saveUser(user: UserDto) {
        val currentSize = users.size

        if (currentSize > CLEAN_UP_LIMIT) {
            users.clear()
        }

        users[user.userId] = user
    }

    private companion object {
        const val CLEAN_UP_LIMIT = 10_000
    }
}