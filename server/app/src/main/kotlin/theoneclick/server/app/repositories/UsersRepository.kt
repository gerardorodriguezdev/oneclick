package theoneclick.server.app.repositories

import theoneclick.server.app.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.User

interface UsersRepository {
    fun user(findable: UsersDataSource.Findable): User?
    fun saveUser(user: User)
}

class DefaultUsersRepository(
    private val diskUsersDataSource: UsersDataSource,
    private val memoryUsersDataSource: UsersDataSource,
) : UsersRepository {

    override fun user(findable: UsersDataSource.Findable): User? {
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

    override fun saveUser(user: User) {
        memoryUsersDataSource.saveUser(user)
        diskUsersDataSource.saveUser(user)
    }
}