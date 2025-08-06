package theoneclick.shared.logging

import theoneclick.shared.logging.AppLogger.Companion.TAG

class JvmAppLogger : AppLogger {
    override fun i(message: String) {
        println("$TAG $message")
    }

    override fun i(tag: String, message: String) {
        println("[$tag] $message")
    }

    override fun e(message: String) {
        System.err.println("$TAG $message")
    }

    override fun e(tag: String, message: String) {
        System.err.println("[$tag] $message")
    }
}

actual fun appLogger(): AppLogger = JvmAppLogger()
