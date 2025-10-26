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
    private val commandsParser: CommandsParser,
    private val commandsHandler: CommandsHandler,
) {
    fun start() = runBlocking<Unit> {
        withContext(dispatchersProvider.io()) {
            launch {
                while (isActive) {
                    val commandString = readlnOrNull() ?: continue
                    val command = commandsParser.parse(commandString) ?: continue
                    commandsHandler.execute(command)
                }
            }

            launch {
                while (isActive) {
                    sync()
                    delay(1_000)
                }
            }

            launch {
                while (isActive) {
                    reconnect()
                    delay(30_000)
                }
            }
        }
    }

    //TODO: Allow cancellation
    suspend fun sync() {
        val isUserLogged = authenticationDataSource.isUserLogged() == UserLoggedResult.Logged
        if (isUserLogged) {
            val devices = devicesStore.getDevices()
            homeDataSource.syncDevices(
                request = SyncDevicesRequest(devices = devices)
            )
        }
    }

    //TODO: Allow cancellation
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
}