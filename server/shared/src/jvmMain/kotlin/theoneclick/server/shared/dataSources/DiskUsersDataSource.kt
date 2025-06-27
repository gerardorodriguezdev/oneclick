package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.User
import theoneclick.server.shared.security.Encryptor
import theoneclick.shared.contracts.core.models.Uuid
import java.io.File

class DiskUsersDataSource(
    private val usersDirectory: File,
    private val encryptor: Encryptor,
    private val logger: Logger,
) : UsersDataSource {

    override fun user(findable: UsersDataSource.Findable): User? =
        when (findable) {
            is UsersDataSource.Findable.ByUserId -> findUser { user -> user.userId.value == findable.userId.value }

            is UsersDataSource.Findable.ByUsername ->
                findUser { user -> user.username.value == findable.username.value }
        }

    override fun saveUser(user: User) {
        try {
            val userString = Json.Default.encodeToString(user)
            val encryptedUserBytes = encryptor.encrypt(input = userString).getOrThrow()
            val userFile = userFile(user.userId)
            userFile.writeBytes(encryptedUserBytes)
        } catch (e: Exception) {
            logger.error("Error trying to save user", e)
        }
    }

    private fun findUser(predicate: (user: User) -> Boolean): User? =
        try {
            val userFiles = userFiles()
            userFiles.forEach { userFile ->
                val encryptedUserBytes = userFile.readBytes()
                val userString = encryptor.decrypt(input = encryptedUserBytes).getOrThrow()
                val user = Json.Default.decodeFromString<User>(userString)
                if (predicate(user)) return user
            }
            null
        } catch (e: Exception) {
            logger.error("Error trying to find user", e)
            null
        }

    private fun userFile(userId: Uuid): File = File(usersDirectory, userFileName(userId))

    private fun userFiles(): Array<File> =
        usersDirectory.listFiles { file ->
            file.name.endsWith(USER_FILE_NAME_SUFFIX)
        }

    companion object {
        private const val USERS_DIRECTORY_NAME = "users"
        private const val USER_FILE_NAME_SUFFIX = "user.txt"
        private fun userFileName(userId: Uuid): String = "${userId.value}.$USER_FILE_NAME_SUFFIX"
        fun usersDirectory(storageDirectory: String): File =
            File(storageDirectory, USERS_DIRECTORY_NAME).apply {
                if (!exists()) {
                    mkdirs()
                }
            }
    }
}