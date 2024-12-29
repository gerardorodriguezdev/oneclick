package theoneclick.shared.core.platform

class WasmLocalLogger : LocalLogger {
    override fun i(message: String) = println(message)
}

actual val localLogger: LocalLogger = WasmLocalLogger()
