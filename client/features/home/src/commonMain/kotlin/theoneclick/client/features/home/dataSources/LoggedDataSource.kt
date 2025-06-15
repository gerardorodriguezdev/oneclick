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
import theoneclick.client.features.home.mappers.toHomes
import theoneclick.client.features.home.repositories.HomesRepository.HomesResult
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

internal interface LoggedDataSource {
    fun homes(): Flow<HomesResult>
}

@Inject
internal class RemoteLoggedDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : LoggedDataSource {

    override fun homes(): Flow<HomesResult> =
        flow {
            val response = httpClient.get(ClientEndpoint.HOMES.route)

            when (response.status) {
                HttpStatusCode.OK -> {
                    val responseBody = response.body<HomesResponseDto>()
                    emit(HomesResult.Success(homes = responseBody.homes.toHomes()))
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
