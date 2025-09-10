package theoneclick.server.services.auth.repositories

import theoneclick.server.services.auth.dataSources.base.UsersDataSource
import theoneclick.server.services.auth.dataSources.models.User

interface UsersRepository {
    suspend fun user(findable: UsersDataSource.Findable): User?
    suspend fun saveUser(user: User): Boolean
}

class DefaultUsersRepository(
    private val diskUsersDataSource: UsersDataSource,
    private val memoryUsersDataSource: UsersDataSource,
) : UsersRepository {

    override suspend fun user(findable: UsersDataSource.Findable): User? {
        val memoryUser = memoryUsersDataSource.user(findable)
        if (memoryUser != null) return memoryUser

        val diskUser = diskUsersDataSource.user(findable)
        return if (diskUser != null) {
            memoryUsersDataSource.saveUser(diskUser)
            diskUser
        } else {
            null
        }
    }

    override suspend fun saveUser(user: User): Boolean {
        memoryUsersDataSource.saveUser(user)
        return diskUsersDataSource.saveUser(user)
    }
}
