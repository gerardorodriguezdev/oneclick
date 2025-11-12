package oneclick.client.apps.home.commands

import oneclick.client.apps.home.commands.CommandsHandler.Command
import oneclick.client.apps.home.commands.CommandsParser.CommandParserResult.Error
import oneclick.client.apps.home.commands.CommandsParser.CommandParserResult.Success
import oneclick.client.apps.home.models.Entry
import oneclick.shared.contracts.auth.models.Password.Companion.toPassword
import oneclick.shared.contracts.auth.models.Username.Companion.toUsername

internal object CommandsParser {
    private const val LOGIN_KEY = "login"
    private const val LOGOUT_KEY = "logout"

    private const val USERNAME_ARG = "username"
    private const val PASSWORD_ARG = "password"

    private const val ARG_SEPARATOR = "--"
    private const val ARG_VALUE_SEPARATOR = "="

    fun parse(string: String): CommandParserResult =
        try {
            val stringWithoutSpaces = string.replace(" ", "")
            val entries = stringWithoutSpaces.split(ARG_SEPARATOR)

            val keyString = entries.firstOrNull() ?: return Error("No command key found")
            val argumentsStrings = entries.drop(1)

            toCommand(keyString = keyString, argumentsEntries = argumentsStrings.toArgumentEntries())
        } catch (error: Exception) {
            Error(message = "Exception '${error.stackTraceToString()}' while parsing command")
        }

    private fun List<String>.toArgumentEntries(): Map<String, Entry> {
        val argumentsEntries = map { entryString ->
            val (key, value) = entryString.split(ARG_VALUE_SEPARATOR)
            Entry(key = key, value = value)
        }

        return argumentsEntries.associateBy { entry -> entry.key }
    }

    private fun toCommand(keyString: String, argumentsEntries: Map<String, Entry>): CommandParserResult {
        return when (keyString) {
            LOGIN_KEY -> {
                val username = argumentsEntries[USERNAME_ARG]?.value?.toUsername() ?: return Error("Invalid username")
                val password = argumentsEntries[PASSWORD_ARG]?.value?.toPassword() ?: return Error("Invalid password")
                Command.Login(username = username, password = password).toSuccess()
            }

            LOGOUT_KEY -> Command.Logout.toSuccess()

            else -> Error("No command key found")
        }
    }

    private fun Command.toSuccess(): Success = Success(this)

    sealed interface CommandParserResult {
        data class Success(val command: Command) : CommandParserResult
        data class Error(val message: String) : CommandParserResult
    }
}