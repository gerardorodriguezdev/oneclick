package oneclick.client.apps.home.commands

import oneclick.client.apps.home.commands.CommandsHandler.Command
import oneclick.client.apps.home.models.Entry
import oneclick.shared.contracts.auth.models.Password.Companion.toPassword
import oneclick.shared.contracts.auth.models.Username.Companion.toUsername
import oneclick.shared.contracts.core.models.Uuid.Companion.toUuid

internal object CommandsParser {
    private const val LOGIN_KEY = "login"
    private const val LOGOUT_KEY = "logout"
    private const val SCAN_KEY = "scan"
    private const val CONNECT_KEY = "connect"
    private const val DISCONNECT_KEY = "disconnect"
    private const val REMOVE_KEY = "remove"

    private const val USERNAME_ARG = "username"
    private const val PASSWORD_ARG = "password"
    private const val DEVICE_ID_ARG = "id"

    private const val ARG_SEPARATOR = "--"
    private const val ARG_VALUE_SEPARATOR = "="

    fun parse(string: String): Command? {
        val stringWithoutSpaces = string.replace(" ", "")
        val entries = stringWithoutSpaces.split(ARG_SEPARATOR)

        val keyString = entries.firstOrNull() ?: return null
        val argumentsStrings = entries.drop(1)

        return toCommand(keyString = keyString, argumentsEntries = argumentsStrings.toArgumentEntries())
    }

    private fun List<String>.toArgumentEntries(): Map<String, Entry> {
        val argumentsEntries = map { entryString ->
            val (key, value) = entryString.split(ARG_VALUE_SEPARATOR)
            Entry(key = key, value = value)
        }

        return argumentsEntries.associateBy { entry -> entry.key }
    }

    private fun toCommand(keyString: String, argumentsEntries: Map<String, Entry>): Command? {
        return when (keyString) {
            LOGIN_KEY -> {
                val username = argumentsEntries[USERNAME_ARG]?.value?.toUsername() ?: return null
                val password = argumentsEntries[PASSWORD_ARG]?.value?.toPassword() ?: return null
                Command.Login(username = username, password = password)
            }

            LOGOUT_KEY -> Command.Logout
            SCAN_KEY -> Command.Scan
            CONNECT_KEY -> {
                val id = argumentsEntries[DEVICE_ID_ARG]?.value?.toUuid() ?: return null
                val password = argumentsEntries[PASSWORD_ARG]?.value?.toPassword() ?: return null
                Command.Connect(id = id, password = password)
            }

            DISCONNECT_KEY -> {
                val id = argumentsEntries[DEVICE_ID_ARG]?.value?.toUuid() ?: return null
                Command.Disconnect(id = id)
            }

            REMOVE_KEY -> {
                val id = argumentsEntries[DEVICE_ID_ARG]?.value?.toUuid() ?: return null
                Command.Remove(id = id)
            }

            else -> return null
        }
    }
}