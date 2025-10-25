package oneclick.client.apps.home

import oneclick.client.apps.home.CommandsHandler.Command
import oneclick.client.apps.home.CommandsHandler.Command.*
import oneclick.client.apps.home.dataSources.base.DevicesController
import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.client.shared.network.models.LogoutResult
import oneclick.client.shared.network.models.RequestLoginResult.Error
import oneclick.client.shared.network.models.RequestLoginResult.ValidLogin
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.client.shared.notifications.NotificationsController
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.auth.models.requests.RequestLoginRequest
import oneclick.shared.contracts.core.models.Uuid

internal interface CommandsHandler {
    suspend fun execute(command: Command)

    sealed interface Command {
        data class Login(val username: Username, val password: Password) : Command
        data object Logout : Command
        data object Scan : Command
        data class Connect(val id: Uuid, val password: Password) : Command
    }
}

internal class DefaultCommandsHandler(
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController,
    private val devicesController: DevicesController,
    private val devicesStore: DevicesStore,
) : CommandsHandler {

    override suspend fun execute(command: Command) {
        when (command) {
            is Login -> command.handle()
            is Logout -> command.handle()
            is Scan -> command.handle()
            is Connect -> command.handle()
        }
    }

    private suspend fun Login.handle() {
        val result = authenticationDataSource
            .login(
                request = RequestLoginRequest(
                    username = username,
                    password = password,
                )
            )

        when (result) {
            is ValidLogin -> notificationsController.showSuccessNotification("Login successful")
            is Error -> notificationsController.showErrorNotification("Login failed")
        }
    }

    //TODO: Clear device store on logout
    private suspend fun Logout.handle() {
        val result = authenticationDataSource.logout()
        when (result) {
            is LogoutResult.Success -> notificationsController.showSuccessNotification("Logout successful")
            is LogoutResult.Error -> notificationsController.showErrorNotification("Logout failed")
        }
    }

    private suspend fun Scan.handle() {
        val devices = devicesController.scan()
        devices.forEach { device ->
            notificationsController.showSuccessNotification("Device found with id: ${device.value}")
        }
    }

    private suspend fun Connect.handle() {
        devicesController
            .connect(
                id = id,
                password = password,
            )
            .collect { device -> devicesStore.updateDevice(device) }
    }
}