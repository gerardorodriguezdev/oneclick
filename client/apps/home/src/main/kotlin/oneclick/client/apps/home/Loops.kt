package oneclick.client.apps.home

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import oneclick.client.apps.home.dataSources.base.DevicesController
import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.client.apps.home.dataSources.base.HomeDataSource
import oneclick.client.shared.network.models.UserLoggedResult
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.client.shared.notifications.NotificationsController
import oneclick.shared.contracts.homes.models.requests.SyncDevicesRequest

internal class Loops(
    private val authenticationDataSource: AuthenticationDataSource,
    private val devicesStore: DevicesStore,
    private val homeDataSource: HomeDataSource,
    private val devicesController: DevicesController,
    private val notificationsController: NotificationsController,
) {
    //TODO: Add interval
    suspend fun syncLoop() {
        val isUserLogged = authenticationDataSource.isUserLogged() == UserLoggedResult.Logged
        if (isUserLogged) {
            val devices = devicesStore.getDevices()
            homeDataSource.syncDevices(
                request = SyncDevicesRequest(devices = devices)
            )
        }
    }

    //TODO: Add interval
    suspend fun reconnectLoop() {
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
                            notificationsController.showSuccessNotification("Device reconnected")
                        } else {
                            notificationsController.showErrorNotification("Error reconnecting device")
                        }
                    }
                }.awaitAll()
            }
        }
    }
}