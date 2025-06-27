package theoneclick.server.shared.dataSources.base

import theoneclick.server.shared.models.User
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.Uuid

interface UsersDataSource {
    fun user(findable: Findable): User?
    fun saveUser(user: User)

    sealed interface Findable {
        data class ByUserId(val userId: Uuid) : Findable
        data class ByUsername(val username: Username) : Findable
    }
}