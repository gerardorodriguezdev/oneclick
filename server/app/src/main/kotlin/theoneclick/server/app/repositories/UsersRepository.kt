package theoneclick.server.app.repositories

import theoneclick.server.app.dataSources.base.UsersDataSource
import theoneclick.server.app.models.dtos.UserDto

interface UsersRepository {
    fun user(findable: UsersDataSource.Findable): UserDto?
    fun saveUser(user: UserDto)
}

class DefaultUsersRepository(
    private val diskUsersDataSource: UsersDataSource,
    private val memoryUsersDataSource: UsersDataSource,
) : UsersRepository {

    override fun user(findable: UsersDataSource.Findable): UserDto? {
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

    override fun saveUser(user: UserDto) {
        memoryUsersDataSource.saveUser(user)
        diskUsersDataSource.saveUser(user)
    }
}