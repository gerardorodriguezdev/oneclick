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
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

internal interface HomesDataSource {
    fun homes(
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto,
    ): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>>
}

@Inject
internal class RemoteHomesDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : HomesDataSource {

    override fun homes(
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto,
    ): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>> =
        flow {
            val response = httpClient.get(ClientEndpoint.HOMES.route) {
                parameter("pageSize", pageSize.value.toString())
                parameter("pageIndex", currentPageIndex.value.toString())
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
