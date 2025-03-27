package theoneclick.shared.core.platform

class JvmAppLogger : AppLogger {
    override fun i(message: String) = println(message)
}

actual fun appLogger(): AppLogger = JvmAppLogger()
