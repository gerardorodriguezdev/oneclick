package oneclick.client.apps.features.home.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import oneclick.client.apps.features.home.models.HomesEntry
import oneclick.client.apps.features.home.models.HomesResult
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import oneclick.shared.contracts.homes.models.requests.HomesRequest
import oneclick.shared.contracts.homes.models.responses.HomesResponse
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

internal interface HomesDataSource {
    suspend fun homes(request: HomesRequest): HomesResult
}

@Inject
internal class RemoteHomesDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : HomesDataSource {

    override suspend fun homes(request: HomesRequest): HomesResult =
        withContext(dispatchersProvider.io()) {
            try {
                val response = httpClient.post(ClientEndpoint.USER_HOMES.route) {
                    setBody(request)
                }

                when (response.status) {
                    HttpStatusCode.OK -> {
                        val homesResponse = response.body<HomesResponse>()
                        val data = homesResponse.data
                        val homesResult =
                            HomesResult.Success(
                                homesEntry = data?.let {
                                    HomesEntry(
                                        homes = data.homes,
                                        pageIndex = data.pageIndex,
                                        canRequestMore = data.canRequestMore,
                                    )
                                }
                            )
                        homesResult
                    }

                    else -> HomesResult.Error
                }
            } catch (error: Exception) {
                appLogger.e("Exception '${error.stackTraceToString()}' while getting homes")
                HomesResult.Error
            }
        }
}
