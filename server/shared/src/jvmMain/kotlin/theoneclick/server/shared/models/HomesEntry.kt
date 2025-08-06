package theoneclick.server.shared.models

import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.UniqueList
import theoneclick.shared.contracts.core.models.Uuid

data class HomesEntry(
    val userId: Uuid,
    val homes: UniqueList<Home>,
) : UniqueList.KeyProvider {
    override val key: String = userId.value
}
