package oneclick.server.services.app.endpoints

import io.ktor.server.response.*
import io.ktor.server.routing.*
import oneclick.server.services.app.dataSources.models.HomesEntry
import oneclick.server.services.app.plugins.authentication.requireUserJwtCredentials
import oneclick.server.services.app.plugins.authentication.userAuthentication
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.shared.contracts.core.models.PaginationResult
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import oneclick.shared.contracts.homes.models.requests.HomesRequest
import oneclick.shared.contracts.homes.models.responses.HomesResponse
import oneclick.shared.contracts.homes.models.responses.HomesResponse.Data

internal fun Routing.userHomesEndpoint(homesRepository: HomesRepository) {
    userAuthentication {
        post(ClientEndpoint.USER_HOMES.route) { homesRequest: HomesRequest ->
            val userId = requireUserJwtCredentials().userId
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
