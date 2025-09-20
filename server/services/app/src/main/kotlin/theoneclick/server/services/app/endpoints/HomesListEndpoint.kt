package theoneclick.server.services.app.endpoints

import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.services.app.dataSources.models.HomesEntry
import theoneclick.server.services.app.plugins.authentication.defaultAuthentication
import theoneclick.server.services.app.plugins.authentication.requireJwtCredentials
import theoneclick.server.services.app.repositories.HomesRepository
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.homes.models.requests.HomesRequest
import theoneclick.shared.contracts.homes.models.responses.HomesResponse
import theoneclick.shared.contracts.homes.models.responses.HomesResponse.Data

internal fun Routing.homesListEndpoint(homesRepository: HomesRepository) {
    defaultAuthentication {
        post(ClientEndpoint.HOMES.route) { homesRequest: HomesRequest ->
            val userId = requireJwtCredentials().userId
            handleUserAvailable(
                userId = userId,
                homesRepository = homesRepository,
                homesRequest = homesRequest,
            )
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
                canRequestMore = homesEntry.pageIndex < homesEntry.totalPages,
            )
        )
    )
}
