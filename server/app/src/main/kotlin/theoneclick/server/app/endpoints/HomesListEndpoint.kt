package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.base.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.extensions.requireToken
import theoneclick.server.app.models.HomesEntry
import theoneclick.server.app.models.User
import theoneclick.server.app.repositories.HomesRepository
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.PositiveLong
import theoneclick.shared.contracts.core.models.requests.HomesRequestDto
import theoneclick.shared.contracts.core.models.responses.HomesResponseDto
import theoneclick.shared.contracts.core.models.responses.HomesResponseDto.DataDto
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

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
                handleUserAvailable(
                    user = user,
                    homesRepository = homesRepository,
                    homesRequestDto = homesRequestDto,
                )
            }
        }
    }
}

private suspend fun RoutingContext.handleUserAvailable(
    user: User,
    homesRepository: HomesRepository,
    homesRequestDto: HomesRequestDto
) {
    val homesEntry = homesRepository.homesEntry(
        userId = user.userId,
        pageSize = homesRequestDto.pageSize,
        currentPageIndex = homesRequestDto.pageIndex
    )

    if (homesEntry == null) {
        handleNoHomesEntry()
    } else {
        val requestLastModified = homesRequestDto.lastModified?.value
        val homesEntryLastModified = homesEntry.value.lastModified.value

        if (requestLastModified != null && homesEntryLastModified <= requestLastModified) {
            handleNotChanged()
        } else {
            handleSuccess(homesEntry)
        }
    }
}

private suspend fun RoutingContext.handleNoHomesEntry() {
    call.respond(HomesResponseDto(data = null))
}

private suspend fun RoutingContext.handleNotChanged() {
    call.respond(
        HomesResponseDto(
            data = DataDto.NotChanged
        )
    )
}

private suspend fun RoutingContext.handleSuccess(homesEntry: PaginationResult<HomesEntry>) {
    call.respond(
        HomesResponseDto(
            data = DataDto.Success(
                lastModified = PositiveLong.unsafe(homesEntry.value.lastModified.value),
                value = homesEntry.value.homes,
                pageIndex = homesEntry.pageIndex,
                canRequestMore = homesEntry.pageIndex.value < homesEntry.totalPages.value,
            )
        )
    )
}