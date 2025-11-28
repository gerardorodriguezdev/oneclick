package oneclick.server.services.app.dataSources.models

import kotlinx.serialization.Serializable
import oneclick.server.shared.authentication.models.HashedPassword
import oneclick.server.shared.authentication.models.RegistrationCode
import oneclick.shared.contracts.auth.models.Username

@Serializable
internal class RegistrableUser(
    val registrationCode: RegistrationCode,
    val username: Username,
    val hashedPassword: HashedPassword,
)