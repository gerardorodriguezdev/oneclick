package theoneclick.shared.core.platform

import android.util.Log

class AndroidAppLogger : AppLogger {
    override fun i(message: String) {
        Log.i("AppLogger", message)
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun e(message: String) {
        Log.e("AppLogger", message)
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
}

actual fun appLogger(): AppLogger = AndroidAppLogger()