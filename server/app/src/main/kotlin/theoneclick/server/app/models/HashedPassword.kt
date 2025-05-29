package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.server.app.platform.SecurityUtils

@Serializable
@JvmInline
value class HashedPassword(val value: String) {

    companion object {
        fun SecurityUtils.create(value: String): HashedPassword = HashedPassword(value)
    }
}
