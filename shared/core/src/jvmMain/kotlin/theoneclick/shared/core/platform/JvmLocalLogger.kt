package theoneclick.shared.core.platform

class JvmLocalLogger : LocalLogger {
    override fun i(message: String) = println(message)
}

actual val localLogger: LocalLogger = JvmLocalLogger()
