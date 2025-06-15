package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.server.app.security.UuidProvider

@JvmInline
@Serializable
value class Uuid private constructor(val value: String) {

    companion object Companion {
        fun UuidProvider.create(value: String): Uuid = Uuid(value)
    }
}