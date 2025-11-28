package oneclick.server.services.app.repositories

import oneclick.server.services.app.dataSources.base.RegistrableUsersDataSource
import oneclick.server.services.app.dataSources.models.RegistrableUser
import oneclick.server.shared.authentication.models.RegistrationCode

internal interface RegistrableUsersRepository {
    suspend fun saveRegistrableUser(registrableUser: RegistrableUser): Boolean
    suspend fun registrableUser(registrationCode: RegistrationCode): RegistrableUser?
    suspend fun deleteRegistrableUser(registrationCode: RegistrationCode): Boolean
}

internal class DefaultRegistrableUsersRepository(
    private val diskRegistrableUsersDataSource: RegistrableUsersDataSource,
    private val memoryRegistrableUsersDataSource: RegistrableUsersDataSource,
) : RegistrableUsersRepository {

    override suspend fun registrableUser(registrationCode: RegistrationCode): RegistrableUser? {
        val memoryRegistrableUser = memoryRegistrableUsersDataSource.registrableUser(registrationCode)
        if (memoryRegistrableUser != null) return memoryRegistrableUser

        val diskRegistrableUser = diskRegistrableUsersDataSource.registrableUser(registrationCode)
        return if (diskRegistrableUser != null) {
            memoryRegistrableUsersDataSource.saveRegistrableUser(diskRegistrableUser)
            diskRegistrableUser
        } else {
            null
        }
    }

    override suspend fun saveRegistrableUser(registrableUser: RegistrableUser): Boolean {
        memoryRegistrableUsersDataSource.saveRegistrableUser(registrableUser)
        return diskRegistrableUsersDataSource.saveRegistrableUser(registrableUser)
    }

    override suspend fun deleteRegistrableUser(registrationCode: RegistrationCode): Boolean {
        memoryRegistrableUsersDataSource.deleteRegistrableUser(registrationCode)
        return diskRegistrableUsersDataSource.deleteRegistrableUser(registrationCode)
    }
}