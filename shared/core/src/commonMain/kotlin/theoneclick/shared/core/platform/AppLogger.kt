package theoneclick.shared.core.platform

interface AppLogger {
    fun i(message: String)
    fun e(message: String)
}

class EmptyAppLogger : AppLogger {
    override fun i(message: String) {}
    override fun e(message: String) {}
}

expect fun appLogger(): AppLogger