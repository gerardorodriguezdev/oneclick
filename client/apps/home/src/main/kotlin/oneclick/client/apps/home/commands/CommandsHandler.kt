package oneclick.client.apps.home.commands

import oneclick.client.apps.home.DevicesController
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
        data class Disconnect(val id: Uuid) : Command
        data class Remove(val id: Uuid) : Command
    }
}

internal class DefaultCommandsHandler(
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController, //TODO: Needs to be immediate
    private val devicesController: DevicesController,
) : CommandsHandler {

    override suspend fun execute(command: CommandsHandler.Command) {
        when (command) {
            is CommandsHandler.Command.Login -> command.handle()
            is CommandsHandler.Command.Logout -> command.handle()
            is CommandsHandler.Command.Scan -> command.handle()
            is CommandsHandler.Command.Connect -> command.handle()
            is CommandsHandler.Command.Disconnect -> command.handle()
            is CommandsHandler.Command.Remove -> command.handle()
        }
    }

    private suspend fun CommandsHandler.Command.Login.handle() {
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

    private suspend fun CommandsHandler.Command.Logout.handle() {
        val result = authenticationDataSource.logout()
        when (result) {
            is LogoutResult.Success -> notificationsController.showSuccessNotification("Logout successful")
            is LogoutResult.Error -> notificationsController.showErrorNotification("Logout failed")
        }
    }

    private suspend fun CommandsHandler.Command.Scan.handle() {
        val devices = devicesController.scan()
        if (devices.isEmpty()) {
            notificationsController.showErrorNotification("No devices found")
        } else {
            devices.forEach { device ->
                notificationsController.showSuccessNotification("Device found with id: ${device.value}")
            }
        }
    }

    private suspend fun CommandsHandler.Command.Connect.handle() {
        val connectedResult = devicesController.connect(
            id = id,
            password = password,
        )
        if (connectedResult) {
            notificationsController.showSuccessNotification("Device connected")
        } else {
            notificationsController.showErrorNotification("Error connecting device")
        }
    }

    private suspend fun CommandsHandler.Command.Disconnect.handle() {
        val disconnectedResult = devicesController.disconnect(id = id)
        if (disconnectedResult) {
            notificationsController.showSuccessNotification("Device disconnected")
        } else {
            notificationsController.showErrorNotification("Error disconnecting device")
        }
    }

    private suspend fun CommandsHandler.Command.Remove.handle() {
        val removedResult = devicesController.remove(id = id)
        if (removedResult) {
            notificationsController.showSuccessNotification("Device removed")
        } else {
            notificationsController.showErrorNotification("Error removing device")
        }
    }
}