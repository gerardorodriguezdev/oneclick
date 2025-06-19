package theoneclick.server.app.dataSources

import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import theoneclick.server.app.models.dtos.UserDto
import theoneclick.server.app.security.Encryptor
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.UsernameDto
import theoneclick.shared.contracts.core.dtos.UuidDto
import java.io.File

interface UsersDataSource {
    fun user(findable: Findable): UserDto?
    fun saveUser(user: UserDto)

    sealed interface Findable {
        data class ByUserId(val userId: UuidDto) : Findable
        data class ByToken(val token: TokenDto) : Findable
        data class ByUsername(val username: UsernameDto) : Findable
    }
}

class MemoryUsersDataSource : UsersDataSource {
    private val users: MutableMap<UuidDto, UserDto> = mutableMapOf()

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

class DiskUsersDataSource(
    private val usersDirectory: File,
    private val encryptor: Encryptor,
    private val logger: Logger,
) : UsersDataSource {

    override fun user(findable: UsersDataSource.Findable): UserDto? =
        when (findable) {
            is UsersDataSource.Findable.ByUserId -> findUser { user -> user.userId.value == findable.userId.value }

            is UsersDataSource.Findable.ByToken ->
                findUser { user -> user.sessionToken?.token?.value == findable.token.value }

            is UsersDataSource.Findable.ByUsername ->
                findUser { user -> user.username.value == findable.username.value }
        }

    override fun saveUser(user: UserDto) {
        try {
            val userString = Json.encodeToString(user)
            val encryptedUserBytes = encryptor.encrypt(input = userString).getOrThrow()
            val userFile = userFile(user.userId)
            userFile.writeBytes(encryptedUserBytes)
        } catch (e: Exception) {
            logger.error("Error trying to save user", e)
        }
    }

    private fun findUser(predicate: (user: UserDto) -> Boolean): UserDto? =
        try {
            val userFiles = userFiles()
            userFiles.forEach { userFile ->
                val encryptedUserBytes = userFile.readBytes()
                val userString = encryptor.decrypt(input = encryptedUserBytes).getOrThrow()
                val user = Json.decodeFromString<UserDto>(userString)
                if (predicate(user)) return user
            }
            null
        } catch (e: Exception) {
            logger.error("Error trying to find user", e)
            null
        }

    private fun userFile(userId: UuidDto): File = File(usersDirectory, userFileName(userId))

    private fun userFiles(): Array<File> =
        usersDirectory.listFiles { file ->
            file.name.endsWith(USER_FILE_NAME_SUFFIX)
        }

    companion object {
        private const val USERS_DIRECTORY_NAME = "users"
        private const val USER_FILE_NAME_SUFFIX = "user.txt"
        private fun userFileName(userId: UuidDto): String = "${userId.value}.$USER_FILE_NAME_SUFFIX"
        fun usersDirectory(storageDirectory: String): File =
            File(storageDirectory, USERS_DIRECTORY_NAME).apply {
                if (!exists()) {
                    mkdirs()
                }
            }
    }
}
