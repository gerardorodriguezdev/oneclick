package oneclick.client.apps.home.commands

import oneclick.client.apps.home.DevicesController
import oneclick.client.shared.network.models.LogoutResult
import oneclick.client.shared.network.models.RequestLoginResult.Error
import oneclick.client.shared.network.models.RequestLoginResult.ValidLogin
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.auth.models.requests.RequestLoginRequest
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.logging.AppLogger

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
    private val logger: AppLogger,
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
            is ValidLogin -> logger.i("Login successful")
            is Error -> logger.e("Login failed")
        }
    }

    private suspend fun CommandsHandler.Command.Logout.handle() {
        val result = authenticationDataSource.logout()
        when (result) {
            is LogoutResult.Success -> logger.i("Logout successful")
            is LogoutResult.Error -> logger.e("Logout failed")
        }
    }

    private suspend fun CommandsHandler.Command.Scan.handle() {
        val devices = devicesController.scan()
        if (devices.isEmpty()) {
            logger.e("No devices found")
        } else {
            devices.forEach { device ->
                logger.i("Device found with id: ${device.value}")
            }
        }
    }

    private suspend fun CommandsHandler.Command.Connect.handle() {
        val connectedResult = devicesController.connect(
            id = id,
            password = password,
        )
        if (connectedResult) {
            logger.i("Device connected")
        } else {
            logger.e("Error connecting device")
        }
    }

    private suspend fun CommandsHandler.Command.Disconnect.handle() {
        val disconnectedResult = devicesController.disconnect(id = id)
        if (disconnectedResult) {
            logger.i("Device disconnected")
        } else {
            logger.e("Error disconnecting device")
        }
    }

    private suspend fun CommandsHandler.Command.Remove.handle() {
        val removedResult = devicesController.remove(id = id)
        if (removedResult) {
            logger.i("Device removed")
        } else {
            logger.e("Error removing device")
        }
    }
}