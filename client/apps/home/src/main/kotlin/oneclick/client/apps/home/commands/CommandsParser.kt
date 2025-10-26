package oneclick.client.apps.home.commands

internal interface CommandsParser {
    fun parse(string: String): CommandsHandler.Command?
}