package oneclick.client.apps.home

import kotlinx.coroutines.*
import oneclick.client.apps.home.commands.CommandsHandler
import oneclick.client.apps.home.commands.CommandsParser
import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.client.apps.home.dataSources.base.HomeDataSource
import oneclick.client.shared.network.models.UserLoggedResult
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.shared.contracts.homes.models.requests.SyncDevicesRequest
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

internal class Entrypoint(
    private val dispatchersProvider: DispatchersProvider,
    private val authenticationDataSource: AuthenticationDataSource,
    private val devicesStore: DevicesStore,
    private val homeDataSource: HomeDataSource,
    private val devicesController: DevicesController,
    private val logger: AppLogger,
    private val commandsHandler: CommandsHandler,
) {
    private var syncJob: Job? = null
    private var reconnectJob: Job? = null

    fun start() = runBlocking<Unit> {
        withContext(dispatchersProvider.io()) {
            launch {
                while (isActive) {
                    print("> ")
                    val commandString = readlnOrNull()?.trim() ?: continue
                    val command = CommandsParser.parse(commandString) ?: continue
                    commandsHandler.execute(command)
                }
            }

            launch {
                while (isActive) {
                    syncJob?.cancel()
                    syncJob = launch { sync() }
                    delay(SYNC_INTERVAL)
                }
            }

            launch {
                while (isActive) {
                    reconnectJob?.cancel()
                    reconnectJob = launch { reconnect() }
                    delay(RECONNECT_INTERVAL)
                }
            }
        }
    }

    suspend fun sync() {
        val isUserLogged = authenticationDataSource.isUserLogged() == UserLoggedResult.Logged
        if (isUserLogged) {
            val devices = devicesStore.getDevices()
            homeDataSource.syncDevices(
                request = SyncDevicesRequest(devices = devices)
            )
        }
    }

    suspend fun reconnect() {
        val authenticatedDevices = devicesController.authenticatedDevices()
        if (authenticatedDevices.isNotEmpty()) {
            val notConnectedDevices =
                authenticatedDevices.filter { authenticatedDevice -> !authenticatedDevice.isConnected }
            notConnectedDevices.reconnectAll()
        }
    }

    private suspend fun List<DevicesController.AuthenticatedDevice>.reconnectAll() {
        coroutineScope {
            launch {
                map { device ->
                    async {
                        val reconnectedResult = devicesController.reconnect(device.id)
                        if (reconnectedResult) {
                            logger.i("Device reconnected")
                        } else {
                            logger.e("Error reconnecting device")
                        }
                    }
                }.awaitAll()
            }
        }
    }

    private companion object {
        const val SYNC_INTERVAL = 1_000L
        const val RECONNECT_INTERVAL = 30_000L
    }
}