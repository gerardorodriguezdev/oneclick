package theoneclick.server.core.models

import kotlinx.serialization.Serializable
import theoneclick.server.core.platform.SecurityUtils

@Serializable
@JvmInline
value class HashedPassword(val value: String) {

    companion object {
        fun SecurityUtils.create(value: String): HashedPassword = HashedPassword(value)
    }
}
