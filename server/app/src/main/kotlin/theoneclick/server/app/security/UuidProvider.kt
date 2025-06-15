package theoneclick.server.app.security

import theoneclick.shared.contracts.core.dtos.UuidDto
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

interface UuidProvider {
    fun uuid(): UuidDto
}

class DefaultUuidProvider : UuidProvider {
    @OptIn(ExperimentalUuidApi::class)
    override fun uuid(): UuidDto = UuidDto.unsafe(value = UUID.randomUUID().toString())
}
