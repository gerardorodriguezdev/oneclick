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
import theoneclick.client.features.home.models.GenericResult
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.requests.HomesRequestDto
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

internal interface HomesDataSource {
    fun homes(request: HomesRequestDto): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>>
}

@Inject
internal class RemoteHomesDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : HomesDataSource {

    override fun homes(request: HomesRequestDto): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>> =
        flow {
            val response = httpClient.post(ClientEndpoint.HOMES.route) {
                setBody(request)
            }

            Result

            when (response.status) {
                HttpStatusCode.OK -> {
                    val homesResponse = response.body<HomesResponseDto>()
                    emit(GenericResult.Success(value = homesResponse.paginationResultDto))
                }

                else -> emit(GenericResult.Error)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while getting homes")
                emit(GenericResult.Error)
            }
            .flowOn(dispatchersProvider.io())
}
