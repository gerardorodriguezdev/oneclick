package theoneclick.shared.core.platform

class JvmAppLogger : AppLogger {
    override fun i(message: String) = println("[AppLogger] $message")
    override fun i(tag: String, message: String) = println("[$tag] $message")
    override fun e(message: String) = System.err.println("[AppLogger] $message")
    override fun e(tag: String, message: String) = System.err.println("[$tag] $message")
}

actual fun appLogger(): AppLogger = JvmAppLogger()
