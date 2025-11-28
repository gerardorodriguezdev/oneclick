package oneclick.server.services.app.dataSources

import oneclick.server.services.app.dataSources.base.RegistrableUsersDataSource
import oneclick.server.services.app.dataSources.models.RegistrableUser
import oneclick.server.shared.authentication.models.RegistrationCode
import java.util.concurrent.ConcurrentHashMap

internal class MemoryRegistrableUsersDataSource(
    private val registrableUsers: ConcurrentHashMap<RegistrationCode, RegistrableUser> = ConcurrentHashMap(),
) : RegistrableUsersDataSource {

    override suspend fun saveRegistrableUser(registrableUser: RegistrableUser): Boolean {
        if (registrableUsers.size > CLEAN_UP_LIMIT) {
            registrableUsers.clear()
        }

        registrableUsers[registrableUser.registrationCode] = registrableUser
        return true
    }

    override suspend fun registrableUser(registrationCode: RegistrationCode): RegistrableUser? =
        registrableUsers[registrationCode]

    override suspend fun deleteRegistrableUser(registrationCode: RegistrationCode): Boolean {
        registrableUsers.remove(registrationCode)
        return true
    }

    private companion object {
        const val CLEAN_UP_LIMIT = 10_000
    }
}
