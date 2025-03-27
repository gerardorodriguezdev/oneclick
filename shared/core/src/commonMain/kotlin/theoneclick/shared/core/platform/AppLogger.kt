package theoneclick.shared.core.platform

interface AppLogger {
    fun i(message: String)
}

class EmptyAppLogger : AppLogger {
    override fun i(message: String) {}
}

expect fun appLogger(): AppLogger