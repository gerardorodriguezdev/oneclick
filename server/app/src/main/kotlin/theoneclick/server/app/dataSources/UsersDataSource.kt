package theoneclick.server.app.dataSources

import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import theoneclick.server.app.models.Token
import theoneclick.server.app.models.User
import theoneclick.server.app.models.Username
import theoneclick.server.app.models.Uuid
import theoneclick.server.app.security.Encryptor
import java.io.File

interface UsersDataSource {
    fun user(sessionToken: Token): User?
    fun user(username: Username): User?
    fun saveUser(user: User)
}

class FileSystemUsersDataSource(
    storageDirectory: String,
    private val encryptor: Encryptor,
    private val logger: Logger,
) : UsersDataSource {
    private val usersDirectory = File(storageDirectory, USERS_DIRECTORY_NAME)

    override fun user(sessionToken: Token): User? = findUser { user -> user.sessionToken?.token == sessionToken.value }

    override fun user(username: Username): User? = findUser { user -> user.username.value == username.value }

    override fun saveUser(user: User) {
        try {
            val userString = Json.encodeToString(user)
            val encryptedUserBytes = encryptor.encrypt(input = userString).getOrThrow()
            val userFile = userFile(user.id)
            userFile.writeBytes(encryptedUserBytes)
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    private fun findUser(predicate: (user: User) -> Boolean): User? =
        try {
            val userFiles = userFiles()
            userFiles.forEach { userFile ->
                val encryptedUserBytes = userFile.readBytes()
                val userString = encryptor.decrypt(input = encryptedUserBytes).getOrThrow()
                val user = Json.decodeFromString<User>(userString)
                if (predicate(user)) return user
            }
            null
        } catch (e: Exception) {
            logger.error(e)
            null
        }

    private fun userFile(userId: Uuid): File = File(usersDirectory, userFileName(userId))

    private fun userFiles(): Array<File> =
        usersDirectory.listFiles { fileName -> fileName.endsWith(USER_FILE_NAME_SUFFIX) }

    private companion object {
        const val USERS_DIRECTORY_NAME = "users"
        const val USER_FILE_NAME_SUFFIX = "user.txt"
        fun userFileName(id: Uuid): String = "${id.value}.$USER_FILE_NAME_SUFFIX"
    }
}
