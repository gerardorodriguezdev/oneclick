package oneclick.server.services.app.repositories

import oneclick.server.services.app.dataSources.models.RegistrableUser
import oneclick.server.shared.authentication.models.RegistrationCode

internal interface RegistrableUsersRepository {
    suspend fun saveRegistrableUser(registrableUser: RegistrableUser): Boolean
    suspend fun registrableUser(registrationCode: RegistrationCode): RegistrableUser?
    suspend fun deleteRegistrableUser(registrationCode: RegistrationCode): Boolean
}