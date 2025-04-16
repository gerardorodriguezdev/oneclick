package theoneclick.server.core.dataSources

import kotlinx.serialization.json.Json
import theoneclick.server.core.dataSources.UserDataSource.Companion.FILE_NAME
import theoneclick.server.core.models.User
import theoneclick.server.core.models.Username
import theoneclick.server.core.platform.FileSystem
import theoneclick.server.core.platform.PathProvider
import theoneclick.server.core.platform.SecurityUtils

interface UserDataSource {
    fun user(sessionToken: String): User?
    fun user(username: Username): User?
    fun saveUser(user: User)

    companion object {
        const val FILE_NAME = "UserData.txt"
    }
}

class FileSystemUserDataSource(
    private val pathProvider: PathProvider,
    private val securityUtils: SecurityUtils,
    private val fileSystem: FileSystem,
) : UserDataSource {

    override fun user(sessionToken: String): User? {
        val userDataPath = pathProvider.path(FILE_NAME)
        val encryptedUserDataBytes = fileSystem.readBytes(userDataPath)
        if (encryptedUserDataBytes.isEmpty()) return null

        val userDataString = securityUtils.decrypt(input = encryptedUserDataBytes)
        return Json.decodeFromString<User>(userDataString)
    }

    override fun user(username: Username): User? {
        val userDataPath = pathProvider.path(FILE_NAME)
        val encryptedUserDataBytes = fileSystem.readBytes(userDataPath)
        if (encryptedUserDataBytes.isEmpty()) return null

        val userDataString = securityUtils.decrypt(input = encryptedUserDataBytes)
        return Json.decodeFromString<User>(userDataString)
    }

    override fun saveUser(user: User) {
        val userDataPath = pathProvider.path(FILE_NAME)
        val userDataString = Json.encodeToString(user)
        val encryptedUserDataBytes = securityUtils.encrypt(input = userDataString)
        fileSystem.writeBytes(userDataPath, encryptedUserDataBytes)
    }
}
