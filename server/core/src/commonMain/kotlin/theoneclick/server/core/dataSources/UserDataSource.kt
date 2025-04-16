package theoneclick.server.core.dataSources

import kotlinx.serialization.json.Json
import theoneclick.server.core.models.User
import theoneclick.server.core.models.Username
import theoneclick.server.core.platform.FileSystem
import theoneclick.server.core.platform.PathProvider
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.shared.core.models.entities.Uuid

interface UserDataSource {
    fun user(sessionToken: String): User?
    fun user(username: Username): User?
    fun saveUser(user: User)
}

class FileSystemUserDataSource(
    private val pathProvider: PathProvider,
    private val securityUtils: SecurityUtils,
    private val fileSystem: FileSystem,
) : UserDataSource {

    override fun user(sessionToken: String): User? = findUser { user -> user.sessionToken?.value == sessionToken }

    override fun user(username: Username): User? = findUser { user -> user.username == username }

    override fun saveUser(user: User) {
        val userFilePath = pathProvider.path(userFileName(user.id))
        val userString = Json.encodeToString(user)
        val encryptedUserBytes = securityUtils.encrypt(input = userString)
        fileSystem.writeBytes(userFilePath, encryptedUserBytes)
    }

    private fun findUser(predicate: (user: User) -> Boolean): User? {
        val paths = pathProvider.paths(filter = { fileName -> fileName.endsWith(SUFFIX) })
        paths.forEach { path ->
            val encryptedUserBytes = fileSystem.readBytes(path)
            if (encryptedUserBytes.isEmpty()) return@forEach

            val userString = securityUtils.decrypt(input = encryptedUserBytes)
            val user = Json.decodeFromString<User>(userString)
            if (predicate(user)) return user
        }

        return null
    }

    companion object {
        const val SUFFIX = "user.txt"
        fun userFileName(id: Uuid): String = "${id.value}.$SUFFIX"
    }
}
