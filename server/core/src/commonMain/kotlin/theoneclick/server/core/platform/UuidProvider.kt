package theoneclick.server.core.platform

import theoneclick.shared.core.models.entities.Uuid
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid.Companion as KmpUuid

interface UuidProvider {
    fun uuid(): Uuid
}

class DefaultUuidProvider : UuidProvider {
    @OptIn(ExperimentalUuidApi::class)
    override fun uuid(): Uuid =
        Uuid(value = KmpUuid.random().toString())
}
