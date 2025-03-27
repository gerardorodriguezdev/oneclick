package theoneclick.shared.core.platform

import android.util.Log

class AndroidAppLogger : AppLogger {
    override fun i(message: String) {
        Log.i("AppLogger", message)
    }
}

actual fun appLogger(): AppLogger = AndroidAppLogger()