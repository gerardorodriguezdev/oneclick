package theoneclick.server.services.auth.dataSources.base

import theoneclick.server.services.auth.dataSources.models.User
import theoneclick.shared.contracts.auth.models.Username
import theoneclick.shared.contracts.core.models.Uuid

interface UsersDataSource {
    suspend fun user(findable: Findable): User?
    suspend fun saveUser(user: User): Boolean

    sealed interface Findable {
        data class ByUserId(val userId: Uuid) : Findable
        data class ByUsername(val username: Username) : Findable
    }
}