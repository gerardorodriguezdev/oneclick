package theoneclick.shared.core.platform

class WasmAppLogger : AppLogger {
    override fun i(message: String) = println("[AppLogger] $message")
    override fun i(tag: String, message: String) = println("[$tag] $message")
    override fun e(message: String) = println("[AppLogger] $message")
    override fun e(tag: String, message: String) = println("[$tag] $message")
}

actual fun appLogger(): AppLogger = WasmAppLogger()
