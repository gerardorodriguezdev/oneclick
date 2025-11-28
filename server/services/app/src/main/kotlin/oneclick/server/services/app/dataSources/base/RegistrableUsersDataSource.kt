package oneclick.server.services.app.dataSources.base

import oneclick.server.services.app.dataSources.models.RegistrableUser
import oneclick.server.shared.authentication.models.RegistrationCode

internal interface RegistrableUsersDataSource {
    suspend fun saveRegistrableUser(registrableUser: RegistrableUser): Boolean
    suspend fun registrableUser(registrationCode: RegistrationCode): RegistrableUser?
    suspend fun deleteRegistrableUser(registrationCode: RegistrationCode): Boolean
}