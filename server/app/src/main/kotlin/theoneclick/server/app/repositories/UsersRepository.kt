package theoneclick.server.app.repositories

import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.dtos.UserDto
import theoneclick.shared.contracts.core.dtos.UserKeyDto

interface UsersRepository {
    fun user(key: UserKeyDto): UserDto?
    fun saveUser(user: UserDto)
}

class DefaultUsersRepository(
    private val diskUsersDataSource: UsersDataSource,
    private val memoryUsersDataSource: UsersDataSource,
) : UsersRepository {

    override fun user(key: UserKeyDto): UserDto? {
        val inMemoryUser = memoryUsersDataSource.user(key)
        if (inMemoryUser != null) return inMemoryUser

        val inDiskUser = diskUsersDataSource.user(key)
        return if (inDiskUser != null) {
            memoryUsersDataSource.saveUser(inDiskUser)
            inDiskUser
        } else {
            null
        }
    }

    override fun saveUser(user: UserDto) {
        memoryUsersDataSource.saveUser(user)
        diskUsersDataSource.saveUser(user)
    }
}