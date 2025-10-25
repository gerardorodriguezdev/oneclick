package oneclick.client.apps.home

import oneclick.client.apps.home.dataSources.base.DevicesController
import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.client.apps.home.dataSources.base.HomeDataSource
import oneclick.client.shared.network.models.UserLoggedResult
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.shared.contracts.homes.models.requests.SyncDevicesRequest

internal class Loops(
    private val authenticationDataSource: AuthenticationDataSource,
    private val devicesStore: DevicesStore,
    private val homeDataSource: HomeDataSource,
    private val devicesController: DevicesController,
) {
    //TODO: Add interval
    suspend fun syncLoop() {
        val isUserLogged = authenticationDataSource.isUserLogged() == UserLoggedResult.Logged
        if (isUserLogged) {
            val devices = devicesStore.getDevices()
            homeDataSource.syncDevices(
                SyncDevicesRequest(
                    devices = devices
                )
            )
        }
    }

    //TODO: Add interval
    //TODO: This is kept forever
    suspend fun reconnectLoop() {
        val authDevices = devicesController.authenticatedDevices()
        val notConnectedDevices = authDevices.filter { authenticatedDevice -> !authenticatedDevice.isConnected }
        notConnectedDevices.forEach { device ->
            //TODO: Would this stop flow in this collection?
            devicesController
                .reconnect(device.id)
                .collect { device -> devicesStore.updateDevice(device) }
        }
    }
}