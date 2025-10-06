package oneclick.server.shared.auth.security

import oneclick.shared.contracts.core.models.Uuid
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

interface UuidProvider {
    fun uuid(): Uuid
}

class DefaultUuidProvider : UuidProvider {
    @OptIn(ExperimentalUuidApi::class)
    override fun uuid(): Uuid = Uuid.unsafe(value = UUID.randomUUID().toString())
}
