package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.extensions.requireToken
import theoneclick.server.app.repositories.HomesRepository
import theoneclick.server.app.repositories.UsersRepository
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
                val homesEntry = homesRepository.homesEntry(
                    userId = user.userId,
                    pageSize = homesRequestDto.pageSize,
                    currentPageIndex = homesRequestDto.pageIndex
                )

                call.respond(
                    HomesResponseDto(
                        data = homesEntry?.let {
                            HomesResponseDto.Data(
                                lastModified = homesEntry.value.lastModified,
                                value = homesEntry.value.homes,
                                pageIndex = homesEntry.pageIndex,
                                canRequestMore = homesEntry.pageIndex.value < homesEntry.totalPages.value,
                            )
                        }
                    )
                )
            }
        }
    }
}