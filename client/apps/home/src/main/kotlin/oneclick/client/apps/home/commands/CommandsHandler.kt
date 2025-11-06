package oneclick.client.apps.home.commands

import oneclick.client.shared.network.models.LogoutResult
import oneclick.client.shared.network.models.RequestLoginResult.Error
import oneclick.client.shared.network.models.RequestLoginResult.ValidLogin
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.auth.models.requests.LoginRequest.HomeRequestLoginRequest
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.logging.AppLogger

internal interface CommandsHandler {
    suspend fun execute(command: Command)

    sealed interface Command {
        data class Login(val username: Username, val password: Password) : Command
        data object Logout : Command
    }
}

internal class DefaultCommandsHandler(
    private val authenticationDataSource: AuthenticationDataSource,
    private val logger: AppLogger,
    private val homeId: Uuid,
) : CommandsHandler {

    override suspend fun execute(command: CommandsHandler.Command) {
        when (command) {
            is CommandsHandler.Command.Login -> command.handle()
            is CommandsHandler.Command.Logout -> command.handle()
        }
    }

    private suspend fun CommandsHandler.Command.Login.handle() {
        val result = authenticationDataSource
            .login(
                request = HomeRequestLoginRequest(
                    username = username,
                    password = password,
                    homeId = homeId,
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
}