package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.endpoints.HomesListConstants.defaultPageIndex
import theoneclick.server.app.endpoints.HomesListConstants.defaultPageSize
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.extensions.requireToken
import theoneclick.server.app.repositories.HomesRepository
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto.Companion.toNonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto.Companion.toPositiveIntDto
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.homesListEndpoint(
    usersRepository: UsersRepository,
    homesRepository: HomesRepository,
) {
    defaultAuthentication {
        get(ClientEndpoint.HOMES.route) {
            val token = requireToken()
            val user = usersRepository.user(UsersDataSource.Findable.ByToken(token))
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                val pageSize = call.parameters["pageSize"]?.toIntOrNull()?.toPositiveIntDto() ?: defaultPageSize
                val pageIndex = call.parameters["pageIndex"]?.toIntOrNull()?.toNonNegativeIntDto() ?: defaultPageIndex

                val currentPagination = homesRepository.homesEntry(
                    userId = user.userId,
                    pageSize = pageSize,
                    currentPageIndex = pageIndex
                )
                val newPagination = currentPagination?.let {
                    PaginationResultDto(
                        lastModified = currentPagination.lastModified,
                        value = currentPagination.value.homes,
                        pageIndex = currentPagination.pageIndex,
                        totalPages = currentPagination.totalPages,
                    )
                }

                call.respond(
                    HomesResponseDto(
                        paginationResultDto = newPagination,
                    )
                )
            }
        }
    }
}

private object HomesListConstants {
    val defaultPageIndex = NonNegativeIntDto.unsafe(0)
    val defaultPageSize = PositiveIntDto.unsafe(10)
}