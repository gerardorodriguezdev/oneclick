package theoneclick.client.features.home.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.models.HomesEntry.Companion.toHomesEntry
import theoneclick.client.features.home.models.HomesResult
import theoneclick.shared.contracts.core.dtos.requests.HomesRequestDto
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

internal interface HomesDataSource {
    fun homes(request: HomesRequestDto): Flow<HomesResult>
}

@Inject
internal class RemoteHomesDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : HomesDataSource {

    override fun homes(request: HomesRequestDto): Flow<HomesResult> =
        flow {
            val response = httpClient.post(ClientEndpoint.HOMES.route) {
                setBody(request)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val homesResponse = response.body<HomesResponseDto>()
                    val homesResult =
                        when (val data = homesResponse.data) {
                            null -> HomesResult.Success(homesEntry = null)
                            is HomesResponseDto.DataDto.Success -> HomesResult.Success(data.toHomesEntry())
                            is HomesResponseDto.DataDto.NotChanged -> HomesResult.NotChanged
                        }
                    emit(homesResult)
                }

                else -> emit(HomesResult.Error)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while getting homes")
                emit(HomesResult.Error)
            }
            .flowOn(dispatchersProvider.io())
}
