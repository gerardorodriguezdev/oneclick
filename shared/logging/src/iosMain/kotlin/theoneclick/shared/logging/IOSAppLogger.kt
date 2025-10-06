package oneclick.shared.logging

import platform.Foundation.NSLog
import oneclick.shared.logging.AppLogger.Companion.TAG

class IOSAppLogger : AppLogger {
    override fun i(message: String) {
        NSLog("$TAG $message")
    }

    override fun i(tag: String, message: String) {
        NSLog("[$tag] $message")
    }

    override fun e(message: String) {
        NSLog("🔴$TAG $message")
    }

    override fun e(tag: String, message: String) {
        NSLog("🔴$TAG $message")
    }
}

actual fun appLogger(): AppLogger = IOSAppLogger()
