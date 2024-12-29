package theoneclick.server.core.testing.extensions

import io.ktor.util.*

fun StringValuesBuilder.appendIfNotNull(name: String, value: String?) {
    value?.let {
        append(name, value)
    }
}
