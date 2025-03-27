package theoneclick.shared.core.platform

class JvmAppLogger : AppLogger {
    override fun i(message: String) = println("[AppLogger] $message")
    override fun e(message: String) = System.err.println("[AppLogger] $message")
}

actual fun appLogger(): AppLogger = JvmAppLogger()
