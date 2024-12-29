package theoneclick.shared.core.platform

interface LocalLogger {
    fun i(message: String)
}

expect val localLogger: LocalLogger
