package oneclick.client.apps.home.utils

import oneclick.client.apps.home.utils.FileAppLogger.LogLevel.ERROR
import oneclick.client.apps.home.utils.FileAppLogger.LogLevel.INFO
import oneclick.shared.logging.AppLogger
import oneclick.shared.timeProvider.TimeProvider
import java.io.File

internal class FileAppLogger(
    private val file: File,
    private val timeProvider: TimeProvider,
) : AppLogger {

    override fun i(message: String) {
        log(level = INFO, message = message)
    }

    override fun i(tag: String, message: String) {
        log(level = INFO, message = message, tag = tag)
    }

    override fun e(message: String) {
        log(level = ERROR, message = message)
    }

    override fun e(tag: String, message: String) {
        log(level = ERROR, message = message, tag = tag)
    }

    private fun log(level: LogLevel, message: String, tag: String? = null) {
        val level = level.value
        val currentTime = timeProvider.currentTimeMillis()
        val data = listOfNotNull(tag, level, currentTime, message)
        val log = data.joinToString(separator = " - ", postfix = "\n---\n")
        file.appendText(log)
    }

    private enum class LogLevel(val value: String) {
        INFO("INFO"), ERROR("ERROR"),
    }
}
