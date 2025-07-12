package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.extensions.defaultAuthentication
import theoneclick.server.shared.extensions.requireToken
import theoneclick.server.shared.models.HomesEntry
import theoneclick.server.shared.repositories.HomesRepository
import theoneclick.server.shared.repositories.SessionsRepository
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.models.requests.HomesRequest
import theoneclick.shared.contracts.core.models.responses.HomesResponse
import theoneclick.shared.contracts.core.models.responses.HomesResponse.Data

fun Routing.homesListEndpoint(
    sessionsRepository: SessionsRepository,
    homesRepository: HomesRepository,
) {
    defaultAuthentication {
        post(ClientEndpoint.HOMES.route) { homesRequest: HomesRequest ->
            val token = requireToken()
            val userId = sessionsRepository.session(SessionsDataSource.Findable.ByToken(token))?.userId
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                handleUserAvailable(
                    userId = userId,
                    homesRepository = homesRepository,
                    homesRequest = homesRequest,
                )
            }
        }
    }
}

private suspend fun RoutingContext.handleUserAvailable(
    userId: Uuid,
    homesRepository: HomesRepository,
    homesRequest: HomesRequest
) {
    val homesEntry = homesRepository.homesEntry(
        userId = userId,
        pageSize = homesRequest.pageSize,
        currentPageIndex = homesRequest.pageIndex
    )

    if (homesEntry == null) {
        handleNoHomesEntry()
    } else {
        handleSuccess(homesEntry)
    }
}

private suspend fun RoutingContext.handleNoHomesEntry() {
    call.respond(HomesResponse(data = null))
}

private suspend fun RoutingContext.handleSuccess(homesEntry: PaginationResult<HomesEntry>) {
    call.respond(
        HomesResponse(
            data = Data(
                homes = homesEntry.value.homes,
                pageIndex = homesEntry.pageIndex,
                canRequestMore = homesEntry.pageIndex.value < homesEntry.totalPages.value,
            )
        )
    )
}
