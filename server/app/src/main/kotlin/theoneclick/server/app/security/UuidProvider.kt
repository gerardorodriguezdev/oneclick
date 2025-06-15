package theoneclick.server.app.security

import theoneclick.server.app.models.Uuid
import theoneclick.server.app.models.Uuid.Companion.create
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

interface UuidProvider {
    fun uuid(): Uuid
}

class DefaultUuidProvider : UuidProvider {
    @OptIn(ExperimentalUuidApi::class)
    override fun uuid(): Uuid = create(value = UUID.randomUUID().toString())
}
