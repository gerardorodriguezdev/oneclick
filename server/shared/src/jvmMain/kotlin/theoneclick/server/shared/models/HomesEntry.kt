package theoneclick.server.shared.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.UniqueList
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
data class HomesEntry(
    val userId: Uuid,
    val homes: UniqueList<Home>,
) : UniqueList.KeyProvider {
    override val key: String = userId.value
}
