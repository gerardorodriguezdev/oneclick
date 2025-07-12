package theoneclick.shared.logging

import theoneclick.shared.logging.AppLogger.Companion.TAG

class WasmAppLogger : AppLogger {
    override fun i(message: String) {
        println("$TAG $message")
    }

    override fun i(tag: String, message: String) {
        println("[$tag] $message")
    }

    override fun e(message: String) {
        println("🔴$TAG $message")
    }

    override fun e(tag: String, message: String) {
        println("🔴$TAG $message")
    }
}

actual fun appLogger(): AppLogger = WasmAppLogger()
