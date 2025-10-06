package oneclick.server.services.app.dataSources.base

import oneclick.server.services.app.dataSources.models.User
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.core.models.Uuid

internal interface UsersDataSource {
    suspend fun user(findable: Findable): User?
    suspend fun saveUser(user: User): Boolean

    sealed interface Findable {
        data class ByUserId(val userId: Uuid) : Findable
        data class ByUsername(val username: Username) : Findable
    }
}