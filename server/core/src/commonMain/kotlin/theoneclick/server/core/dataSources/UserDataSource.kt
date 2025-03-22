package theoneclick.server.core.dataSources

import kotlinx.serialization.json.Json
import theoneclick.server.core.dataSources.UserDataSource.Companion.FILE_NAME
import theoneclick.server.core.models.UserData
import theoneclick.server.core.platform.FileSystem
import theoneclick.server.core.platform.PathProvider
import theoneclick.server.core.platform.SecurityUtils

interface UserDataSource {
    fun userData(): UserData?
    fun saveUserData(userData: UserData)
    fun removeUserData()

    companion object {
        const val FILE_NAME = "UserData.txt"
    }
}

class FileSystemUserDataSource(
    private val pathProvider: PathProvider,
    private val securityUtils: SecurityUtils,
    private val fileSystem: FileSystem,
) : UserDataSource {

    override fun userData(): UserData? {
        val userDataPath = pathProvider.path(FILE_NAME)
        val encryptedUserDataBytes = fileSystem.readBytes(userDataPath)
        if (encryptedUserDataBytes.isEmpty()) return null

        val userData = securityUtils.decrypt(input = encryptedUserDataBytes)
        return Json.decodeFromString<UserData>(userData)
    }

    override fun saveUserData(userData: UserData) {
        val userDataPath = pathProvider.path(FILE_NAME)
        val userDataString = Json.encodeToString(userData)
        val encryptedUserDataBytes = securityUtils.encrypt(input = userDataString)
        fileSystem.writeBytes(userDataPath, encryptedUserDataBytes)
    }

    override fun removeUserData() {
        val userDataPath = pathProvider.path(FILE_NAME)
        fileSystem.delete(userDataPath)
    }
}
