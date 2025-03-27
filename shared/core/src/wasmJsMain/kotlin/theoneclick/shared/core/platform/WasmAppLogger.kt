package theoneclick.shared.core.platform

class WasmAppLogger : AppLogger {
    override fun i(message: String) = println("[AppLogger] $message")
    override fun e(message: String) = println("[AppLogger] $message")
}

actual fun appLogger(): AppLogger = WasmAppLogger()
