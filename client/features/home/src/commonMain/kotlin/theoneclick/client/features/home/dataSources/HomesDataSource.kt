package theoneclick.client.features.home.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.models.HomesEntry
import theoneclick.client.features.home.models.HomesResult
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.models.requests.HomesRequest
import theoneclick.shared.contracts.core.models.responses.HomesResponse
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

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
                val response = httpClient.post(ClientEndpoint.HOMES.route) {
                    setBody(request)
                }

                when (response.status) {
                    HttpStatusCode.OK -> {
                        val homesResponse = response.body<HomesResponse>()
                        val data = homesResponse.data
                        val homesResult = HomesResult.Success(
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
            } catch (e: Exception) {
                appLogger.e("Exception catched '${e.stackTraceToString()}' while getting homes")
                HomesResult.Error
            }
        }
}
