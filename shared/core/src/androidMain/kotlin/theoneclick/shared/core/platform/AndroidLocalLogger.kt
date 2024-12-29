package theoneclick.shared.core.platform

import android.util.Log

class AndroidLocalLogger : LocalLogger {
    override fun i(message: String) {
        Log.i("LocalLoggerTag", message)
    }
}

actual val localLogger: LocalLogger = AndroidLocalLogger()
