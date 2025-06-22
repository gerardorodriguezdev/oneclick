package theoneclick.server.app.dataSources.base

import theoneclick.server.app.models.dtos.UserDto
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.UsernameDto
import theoneclick.shared.contracts.core.dtos.UuidDto

interface UsersDataSource {
    fun user(findable: Findable): UserDto?
    fun saveUser(user: UserDto)

    sealed interface Findable {
        data class ByUserId(val userId: UuidDto) : Findable
        data class ByToken(val token: TokenDto) : Findable
        data class ByUsername(val username: UsernameDto) : Findable
    }
}