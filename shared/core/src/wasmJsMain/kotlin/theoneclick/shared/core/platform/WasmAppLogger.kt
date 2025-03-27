package theoneclick.shared.core.platform

class WasmAppLogger : AppLogger {
    override fun i(message: String) = println(message)
    override fun e(message: String) = println(message)
}

actual fun appLogger(): AppLogger = WasmAppLogger()
