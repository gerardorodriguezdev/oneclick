package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.extensions.requireToken
import theoneclick.server.app.repositories.HomesRepository
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.requests.HomesRequestDto
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.homesListEndpoint(
    usersRepository: UsersRepository,
    homesRepository: HomesRepository,
) {
    defaultAuthentication {
        post(ClientEndpoint.HOMES.route) { homesRequestDto: HomesRequestDto ->
            val token = requireToken()
            val user = usersRepository.user(UsersDataSource.Findable.ByToken(token))
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                val currentPagination = homesRepository.homesEntry(
                    userId = user.userId,
                    pageSize = homesRequestDto.pageSize,
                    currentPageIndex = homesRequestDto.pageIndex
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