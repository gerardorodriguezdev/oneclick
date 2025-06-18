package theoneclick.server.app.dataSources

import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import theoneclick.server.app.models.dtos.UserDto
import theoneclick.server.app.security.Encryptor
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.UserKeyDto
import theoneclick.shared.contracts.core.dtos.UsernameDto
import theoneclick.shared.contracts.core.dtos.UuidDto
import java.io.File

interface UsersDataSource {
    fun user(key: UserKeyDto): UserDto?
    fun saveUser(user: UserDto)
}

class InMemoryUsersDataSource : UsersDataSource {
    private val users: MutableMap<UserKeyDto, UserDto> = mutableMapOf()

    override fun user(key: UserKeyDto): UserDto? = users[key]

    override fun saveUser(user: UserDto) {
        val currentSize = users.size

        if (currentSize > CLEAN_UP_LIMIT) {
            users.clear()
        }

        users[user.username] = user
    }

    private companion object {
        const val CLEAN_UP_LIMIT = 10_000
    }
}

class FileSystemUsersDataSource(
    private val usersDirectory: File,
    private val encryptor: Encryptor,
    private val logger: Logger,
) : UsersDataSource {

    override fun user(key: UserKeyDto): UserDto? =
        when (key) {
            is TokenDto -> findUser { user -> user.sessionToken?.token?.value == key.value }
            is UsernameDto -> findUser { user -> user.username.value == key.value }
        }

    override fun saveUser(user: UserDto) {
        try {
            val userString = Json.encodeToString(user)
            val encryptedUserBytes = encryptor.encrypt(input = userString).getOrThrow()
            val userFile = userFile(user.id)
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
        private fun userFileName(id: UuidDto): String = "${id.value}.$USER_FILE_NAME_SUFFIX"
        fun usersDirectory(
            storageDirectory: String,
        ): File = File(
            storageDirectory,
            USERS_DIRECTORY_NAME,
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
}
